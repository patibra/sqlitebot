package advancedbot;

import cz.cuni.pogamut.Client.Agent;
import cz.cuni.pogamut.Client.RcvMsgEvent;
import cz.cuni.pogamut.Client.RcvMsgListener;
import cz.cuni.pogamut.MessageObjects.*;
import cz.cuni.pogamut.introspection.PogProp;
import cz.cuni.pogamut.exceptions.ConnectException;
import cz.cuni.pogamut.exceptions.PogamutException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This bot currently is copied code from the hunter bot example,
 * plus a strafing fuction from the amis/loque bot.
 *
 * The part which is mine are the functions dbWrite(database INSERT) and SetNavpoint(database SELECT)
 * which demonstrate the beginning utilization of a sqlite database to persist the bots memory/events
 * for better analysis and planning by the bot in gameplay
 */

public class Main extends Agent {

   String sqliteClass = "org.sqlite.JDBC";
   //String sqliteDBPath = "jdbc:sqlite:/Program Files/Pogamut 2/PogamutPlatform/projects/AdvancedBot/src/advancedbot/sample.db";
   String sqliteDBPath = "jdbc:sqlite:c:/sqlitebot/sample.db";
   //String sqliteDBPath = "jdbc:sqlite:/sqlitebot/sample.db";  //unix

   @PogProp NavPoint chosenNavigationPoint = null;
   @PogProp NavPoint lastNavigationPoint = null;

   //int navSize = memory.getKnownNavPoints().size();
   @PogProp int navCount = 0;

   int hideFromNav = -1; //used with 'hide' behavior

   //behavior = normal,camp,navlog,hide
   @PogProp String behavior = "hide";

   // Create a hash table
   Map navMap = new HashMap();
   //navMap = new TreeMap();        // sorted map

   Map timerMap = new HashMap();

   @PogProp int botYFocus = 0;
   @PogProp double botYMin = 0.0;
   @PogProp double botYMax = 0.0;

   Player enemy = null;
   double gameTime = 0.0;
   double turnTime = 0.0;

   public boolean shouldIdle = false;
   boolean idleState = false;
   double idleTime = 0.0;

   boolean strafingRight = true;

   int tripFlag = 0;
   boolean initFlag = true;

   //@PogProp boolean turn;
   //@PogProp boolean hitWall;

   @PogProp int frags = 0;
   @PogProp int deaths = 0;
   /** how low the health level should be to start collecting healht */
   @PogProp
   public int healthSearchLevel = 90;
   public int healthEvadeLevel = 60;  //try 60
   /** choosen item for the state seeItem */
   protected Item choosenItem = null;
   /** choose med kits for the stateMedKit */
   protected ArrayList<Item> choosenMedKits = null;

   protected ArrayList<Item> choosenItems = null;

   @PogProp
   public boolean useAStar = false;
    /** last enemy which disappeared from agent's view */
    private Player lastEnemy = null;
    /** walking mystic properties - prevent bot from continuous jumping - he will jump only once */
    private boolean jumped;
    /**
     * Stores last unreachable item - item that bot chose and was not able to go to. <br>
     * This setting should prevent bot from stucks.
     */
    protected Item previousChoosenItem = null;

    @PogProp
    public boolean shouldRearm = true;
    @PogProp
    public boolean shouldEngage = true;
    @PogProp
    public boolean shouldPursue = true;
    @PogProp
    public boolean shouldCollectItems = true;

    public boolean shouldHide = false;

    Weapon weaponGet = null;
    Weapon weaponGetPickup = null;
    String runType = null;

   /** Creates a new instance of agent. */
   public Main() {
       super();
       /**
        * set level of logging - see logging documentation, now you will see only more relevant things
        */
       this.log.setLevel(Level.INFO);
       this.platformLog.setLevel(Level.INFO);
   }

   @Override
   protected void postPrepareAgent() {

       // this affects the bot's skill level ... 6 == GODLIKE, 0 == total newbie
       body.initializer.setBotSkillLevel(4);



    // Add key/value pairs to the map
    //navMap.put("x", new Integer(1));
    //navMap.put("y", new Integer(2));
    //navMap.put("z", new Integer(3));

    for (int i=0; i<memory.getKnownNavPoints().size(); i++) {
         navMap.put(Integer.toString(memory.getKnownNavPoints().get(i).ID), new Integer (Integer.toString(i)));
         log.info(i+":"+Integer.toString(memory.getKnownNavPoints().get(i).ID)+":"+memory.getKnownNavPoints().get(i).UnrealID);
    }

      if (lastNavigationPoint == null) {
        log.info("navpoint=null");
        lastNavigationPoint = memory.getKnownNavPoints().get(0);
        chosenNavigationPoint = memory.getKnownNavPoints().get(0);
      }
       
    // Get number of entries in map
    //int size = navMap.size();        // 2
    //log.info("size:"+size);
    //createNavArray();
 
/*
      Class.forName(sqliteClass);
      Connection conn = DriverManager.getConnection(sqliteDBPath);
      Statement stat = conn.createStatement();

      String sql = null;
      sql = "select from_navpoint_id from navpoint where from_navpoint_id = and map_level =  limit 1;";

      log.info("sql:"+sql);
      ResultSet rs = stat.executeQuery(sql);

      boolean flagFound = false;
      while (rs.next()) {
             flagFound = true;
      }

      rs.close();
      conn.close();
 */
}

    /**
     * Main method of the bot's brain - we're going to do some thinking about
     * the situation we're in (how it's unfair to be the bot in the gloomy world
     * of UT2004 :-).
     * <p>
     * Check out the javadoc for this class - there you find a basic concept
     * of this bot.
     */
    @Override
     protected void doLogic() {
        // marking next iteration
        log.fine("doLogic iteration");

       if (initFlag) {
         initFlag = false;
         //for navlog each navpoint can see itself
         if (behavior.equals("navlog")) {
             for (int i=0; i<memory.getKnownNavPoints().size(); i++) {
               try { dbWriteNavpoint(memory.getGameInfo().level.toString(),memory.getKnownNavPoints().get(i).ID,memory.getKnownNavPoints().get(i).ID); } catch (Exception K) { System.out.println("Hello World!:"+i); }
             }
          }
        }

        // navtest begin
        
        //hunt/engage if you are healthy enough or enemy too close, otherwise hide

        int minEngageDistance = Math.round(this.random.nextFloat() * 200) + 400;
        if ((this.memory.getAgentHealth() < this.healthEvadeLevel) &&
            (this.memory.getAgentLocation() != null && this.enemy != null && this.enemy.location != null &&
             Triple.distanceInSpace(this.memory.getAgentLocation(), this.enemy.location) > minEngageDistance)) {

            this.shouldHide = true;
            this.shouldEngage = false;
            this.shouldPursue = false;
        }
        else {
            this.shouldHide = false;
            this.shouldEngage = true;
            this.shouldPursue = true;
        }

        // IF-THEN RULES:
        // 1) see enemy and has better weapon? -> switch to better weapon
        if (this.shouldRearm && this.memory.getSeeAnyEnemy() && this.hasBetterWeapon()) {
            this.stateChangeToBetterWeapon();
            return;
        }

        // 2) do you see enemy?         -> go to PURSUE (start shooting / hunt the enemy)
        if (this.shouldEngage && this.memory.getSeeAnyEnemy() && this.memory.hasAnyLoadedWeapon()) {
            this.stateEngage();
            //if (this.shouldHide) { navigateBot();}
            return;
        }

         // 3) are you shooting?        -> go to STOP_SHOOTING (stop shooting, you've lost your target)
        if (this.memory.isShooting()) {
            this.stateStopShooting();
            return;
        }

       // are you being shot?  -> go to HIT (turn around - try to find your enemy)
        if (this.memory.isBeingDamaged()) {
            this.stateHit();
            //return;
        }

        // have you got enemy to pursue? -> go to the last position of enemy
        if ((this.lastEnemy != null) && (this.shouldPursue) && (this.memory.hasAnyLoadedWeapon())
                && (weaponGetPickup == null)) {
            this.stateGoAtLastEnemyPosition();
            return;
        }

        // 6) are you walking?          -> go to WALKING       (check WAL)
        if (this.memory.isColliding()) {
            log.info("collision");
            //strafingRight = !strafingRight;
            //this.stateWalking();
            //return;
        }

        // 7) do you see item?          -> go to GRAB_ITEM        (pick the most suitable item and run for)
        if (this.shouldCollectItems && this.seeAnyReachableItemAndWantIt()) {
            this.stateSeeItem();
            return;
        }
/*
        // are you hurt?                        -> get yourself some medKit
        if (this.memory.getAgentHealth() < this.healthSearchLevel && this.canRunAlongMedKit()) {
            this.stateMedKit();
            return;
        }
*/
        if (weaponGetPickup == null) {
        if (this.getReachableWeapons()) {
            //return;
        }
        }

        if (weaponGetPickup == null && weaponGet == null) {
          if (this.canRunAlongItems()) {
            //return;
          }
        }

        //random idle
        if (this.shouldIdle) {
            if (idleState == false) {

              int minIdleWait = 20;  //min number of seconds to wait between idles
              if (gameTime > idleTime+minIdleWait) {
                log.info("gameTime:"+gameTime+":idleTime:"+idleTime);
                int myRandIdle = random.nextInt(3)+1;  //1 in x chance of entering idle
                log.info("randIdle:"+myRandIdle);
                if (myRandIdle == 1) {
                  idleState = true;
                  idleTime = gameTime;
                  return;
                }
                //return;
              }
            }

            //idleState = true
            int idleLength = 5; //number of idle seconds
            if (gameTime > idleTime+idleLength) {
                idleState = false;
            }
            else {
                int myRandTurn = random.nextInt(90)-45;  //random looking left/right
                this.body.turnHorizontal(myRandTurn);
                //additional random idle movement/feet ??
                return;
            }
        }
        
 // navtest end

     // no enemy spotted ... just run randomly
     navigateBot();

}

protected void navigateBot() {

        //log.info("navigateBot");
        // if don't have any navigation point chosen
        if (chosenNavigationPoint == null) {
            // let's pick one at random
            try { setNavpoint(behavior); } catch (Exception K) { System.out.println("Hello World!"); }
            //chosenNavigationPoint = memory.getKnownNavPoints().get(random.nextInt(memory.getKnownNavPoints().size()));
            log.fine("navpoint_logic="+chosenNavigationPoint.UnrealID);
            log.fine("map_id_logic="+chosenNavigationPoint.getID());
       }
        // here we're sure the chosenNavigationPoint is not null
        // call method iteratively to get to the navigation point

        //log.info("debugMain");
        if (tripFlag == 0) {
            //tripFlag = 1;

        //if (chosenNavigationPoint == lastNavigationPoint) { return; }
        //if (Triple.distanceInSpace(memory.getAgentLocation(), chosenNavigationPoint.location) < 100) { return; }
//log.info("debugMain2"+chosenNavigationPoint.location.toString());
        if (weaponGet != null) {log.info("debugMain2:"+weaponGet.weaponType.toString()); }
        if (weaponGetPickup != null) {log.info("debugMain2WPickup:"+weaponGetPickup.weaponType.toString()); }
        if ((runType != null) && runType.equals("runto")) {
          log.info("runtopickup");


          if (Triple.distanceInSpace(memory.getAgentLocation(), chosenNavigationPoint.location) < 50) {
               //(!chosenNavigationPoint.isVisible())) {
              runType = null;
              weaponGetPickup = null;
              return;
          }
          body.runToLocation(chosenNavigationPoint.location);
        return;
        }
        if (!gameMap.safeRunToLocation(chosenNavigationPoint.location)) {
            log.info("debugMain4"+chosenNavigationPoint.location.toString());
            weaponGet = null;
        //if (!gameMap.safeRunToLocationNav(chosenNavigationPoint)) {
            // if safeRunToLocation() returns false it means
            //log.info("chosen_dist:"+Triple.distanceInSpace(memory.getAgentLocation(), chosenNavigationPoint.location));
            if (Triple.distanceInSpace(memory.getAgentLocation(), chosenNavigationPoint.location) < 100) {
                // 1) we're at the navpoint
                log.info("I've successfully arrived at navigation point!");
                gameMap.resetPath();
                //Triple campFocusLocation = new Triple (-1520.0,1450.0,-207.0);
                //this.body.turnToLocation(campFocusLocation);
            } else {
                // 2) something bad happens
                log.info("Darn the path is broken :(:"+Triple.distanceInSpace(memory.getAgentLocation(), chosenNavigationPoint.location));
                //this.body.runToLocation(lastNavigationPoint.location);
                if (memory.getSeeAnyNavPoint()) {
                  this.body.runToLocation(memory.getSeeNavPoint().location);
                }
                else {
                  this.body.turnHorizontal(180);
                }

                //Triple campFocusLocation = new Triple (-1520.0,1450.0,-207.0);
                //this.body.turnToLocation(campFocusLocation);
            }
            // nullify chosen navigation point and chose it during the
            // next iteration of the logic
            lastNavigationPoint = chosenNavigationPoint;
            chosenNavigationPoint = null;
        }
//log.info("debugMain3");
        } //tripFlag


}

     /**
     * changes to better weapon that he posseses
     */
    @SuppressWarnings("static-access")
    protected void stateChangeToBetterWeapon() {
        this.log.log(Level.INFO, "Decision is: CHANGE WEAPON");
        if (memory.getAgentLocation() == null || memory.getSeeEnemy() == null || memory.getSeeEnemy().location == null) {
            return;
        }
        AddWeapon weapon = memory.getBetterWeapon(memory.getAgentLocation(), memory.getSeeEnemy().location);
        if (weapon != null) {
            int myRandSleep = random.nextInt(100);
            try {
                Thread.currentThread().sleep(200+myRandSleep);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            body.changeWeapon(weapon);
        }
    }

    /**
     * has better weapon - this magic check goes through weapons in inventory and according to their characteristics
     * decides which is the best - that means which effectiveDistance is lowest and which maximal distance is big enough
     * to reach enemy.
     * </p>
     * <p>
     * Note!: Both effective and maximal distance are guessed and therefore could not work exactly
     * </p>
     */
        protected boolean hasBetterWeapon() {
        Player enemy = memory.getSeeEnemy();
        if (memory.getAgentLocation() == null || enemy == null || enemy.location == null) {
            return false;
        }
        AddWeapon weapon = memory.getBetterWeapon(memory.getAgentLocation(), enemy.location);
        // platformLog.info("Better weapon : " + weapon + "\nWeapons: " + this.memory.getAllWeapons().toString());
        if (weapon == null) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Fired when bot see any enemy.
     * <ol>
     * <li> if have enemyID - checks whether the same enemy is visible, if not, drop him (and stop shooting)
     * <li> if doesn't have enemyID - pick one of the enemy for pursuing
     * <li> if not shooting at enemyID - start shooting
     * <li> if out of ammo - switch to another weapon
     * <li> if enemy is reachable - run to him
     * <li> if enemy is not reachable - stand still (kind a silly, right? :-)
     * </ol>
     */
    protected void stateEngage() {
        this.log.log(Level.INFO, "Decision is: ENGAGE");
        // 1) if have enemyID - checks whether the same enemy is visible, if not, drop ID (and stop shooting)
        if (this.enemy != null) {
            this.lastEnemy = enemy;
            this.enemy = this.memory.getSeePlayer(this.enemy.ID); // refresh information about the enemy,
            // note that even though we've got pointer to the message of the enemy seen, it's still a certain message
            // from a specific time - when new message arrives it's written as a new message
            if (this.enemy == null) {
                if (this.memory.isShooting()) {
                    this.body.stopShoot();
                } // stop shooting, we've lost target
                return;
            }
        }

        // 2) if doesn't have enemy - pick one of the enemy for pursuing
        if (this.enemy == null) {
            this.enemy = this.memory.getSeeEnemy();
            if (this.enemy == null) {
                this.body.stop();
                this.body.stopShoot();
                return;
            }
        }

        log.info("enemy:"+this.enemy.name.toString());

        AddWeapon weapon = null;
        // 3) if out of ammo - switch to another weapon
        if ((!this.memory.hasLoadedWeapon()) && this.memory.hasAnyLoadedWeapon()) {
            platformLog.info("no ammo - switching weapon " + this.memory.hasLoadedWeapon() + " " + this.memory.getAnyWeapon() + "\nCurrent Weapon:" + this.memory.getCurrentWeapon() + "\nWeapons : " + this.memory.getAllWeapons().toString());
            weapon = this.memory.getAnyWeapon();
            if ((weapon != null) && ((memory.getCurrentWeapon() == null) || ((memory.getCurrentWeapon() != null) && (!weapon.weaponType.equals(memory.getCurrentWeapon().weaponType))))) {
                platformLog.info("no ammo - switching weapon: " + weapon);
                this.body.changeWeapon(weapon);
            } else {

            }
        }

        // 4) if not shooting at enemyID - start shooting
        double distance = (Triple.distanceInSpace(this.memory.getAgentLocation(), this.enemy.location));
        if (this.memory.getCurrentWeapon() != null && this.memory.getCurrentWeapon().maxDist > distance) {// it is worth shooting
            platformLog.info("Would like to shoot at enemy!!!");
            if (!this.memory.isShooting()) {
                this.body.shoot(this.enemy);
                log.info("start shooting");
            } else // to turn to enemy - shoot will not turn to enemy during shooting
            {
                this.body.turnToTarget(this.enemy);
                log.info("start turning");
            }
        }

        //if (this.shouldHide) { return; } //don't close distance, follow hide navigation

        // 5) if enemy is far - run to him
        int decentDistance = Math.round(this.random.nextFloat() * 800) + 400;

        if (this.memory.getAgentLocation() != null && this.enemy != null && this.enemy.location != null &&
                Triple.distanceInSpace(this.memory.getAgentLocation(), this.enemy.location) < decentDistance) {
            //if (this.memory.isMoving()) {
            if (1 == 1) {
                //this.body.stop();

            // compute new agent location (to where to run to)
            // note: resolves strafing and pick-ups
            Triple newAgentLocation = getStrafeAroundLocation (this.enemy);
            // strafe to the new location and look at the new focal point..
            this.body.strafeToLocation(newAgentLocation, this.enemy.location);
            }
            else { log.info("not moving"); }
        } else {
            log.info("runToTarget:"+decentDistance);
            this.body.runToTarget(enemy);
            this.jumped = false;
        }
    }

     /**
     * Fired when bot loose enemy from his view <br>
     * He just stops shooting and no more wastes his ammo
     */
    protected void stateStopShooting() {
        this.log.log(Level.INFO, "Decision is: STOP_SHOOTING");
        this.body.stopShoot();
    }

     /**
     * Fired when bot is damaged, it has those options:
     * <ol>
     * <li> He has idea where to turn to from to DAM message
     * <li> He got no idea at all -> turns around
     * </ol>
     */
    protected void stateHit() {
        if (turnTime == 0.0) {
            turnTime = gameTime;
        }
        double waitTimeHit = 0.0;
        //only perform turn if within last waitTimeHit seconds
        if (turnTime >= (gameTime - waitTimeHit)) {
            this.log.log(Level.INFO, "Decision is: HIT");
            this.body.turnHorizontal(180);
                int myRand = random.nextInt(3)+1;

                if (myRand == 1) {
                  log.info("dodge right");
                  Triple dodgeDirection = new Triple (0.0,1.0,0.0);
                  this.body.dodge(dodgeDirection);
                }
                if (myRand == 2) {
                  log.info("dodge left");
                  Triple dodgeDirection = new Triple (0.0,-1.0,0.0);
                  this.body.dodge(dodgeDirection);
                }
                if (myRand == 3) {
                  log.info("jump");
                  this.body.jump();
                }
        }
        else {
            this.log.log(Level.INFO, "HIT ignored");
            if (turnTime+3.0 < (gameTime - waitTimeHit)) {
              turnTime = 0.0;
            }
        }
    }

        /**
     * State pursue is for pursuing enemy who was for example lost behind a corner.
     * How it works?:
     * <ol>
     * <li> initialize properties
     * <li> obtain path to the enemy
     * <li> follow the path - if it reaches the end - set lastEnemy to null - bot would have seen him before or lost him once for all
     * </ol>
     */
    protected void stateGoAtLastEnemyPosition() {
        this.log.log(Level.INFO, "Decision is: PURSUE");
        if (!this.gameMap.safeRunToLocation(lastEnemy.location)) {         // unable to reach the choosen item
            log.info("Ended at the enemy position or failed - > STOP THE CHASE.");
            previousChoosenItem = choosenItem;
            lastEnemy = null;
        }
        return;
    }

    /**
     * Fired when bot is moving, checks few accidents than can happen to him
     * <ol>
     * <li> Wall collision
     * <li> Fell of the bot
     * <li> Bump to another actor of the game
     * </ol>
     */
        protected void stateWalking() {
        this.log.log(Level.INFO, "Decision is: WALKING");

        if (this.memory.isColliding()) {
            if (!this.jumped) {
                this.body.doubleJump();
                this.jumped = true;
            } else {
                this.body.stop();
                this.jumped = false;
            }
        }
        if (this.memory.isFalling()) {
            this.body.sendGlobalMessage("I am flying like a bird:D!");
            this.log.info("I'm flying like an angel to the sky ... it's so high ...");
        }
        if (this.memory.isBumpingToAnotherActor()) {
            this.body.stop();
        }
    }

   /**
     * choose weapon according to the one he is currently holding
     * <ol>
     * <li> has melee and see ranged => pick up ranged
     * <li> has ranged and see melee => pick up melee
     * <li> pick up first weapon he sees
     * </ol>
     *
     * @return the choosen one weapon
     */
    private Weapon chooseWeapon() {
        ArrayList<Weapon> weapons = memory.getSeeReachableWeapons();
        for (Weapon weapon : weapons) {
            // 0) has no weapon in hands
            if (this.memory.getCurrentWeapon() == null) {
                return weapon;
            }
            // 1) weapon is ranged, bot has melee
            if ((this.memory.getCurrentWeapon().melee) && !weapon.isMelee() && !this.memory.hasWeaponOfType(weapon.weaponType)) {
                return weapon;
            }
            // 2) weapon is melee, bot has ranged
            if (!this.memory.getCurrentWeapon().melee && weapon.isMelee() && !this.memory.hasWeaponOfType(weapon.weaponType)) {
                return weapon;
            }
        }
        Weapon chosen = this.memory.getSeeReachableWeapon();
        if (!this.memory.hasWeaponOfType(chosen.weaponType)) {
            return chosen;
        }
        return null;
    }

    /**
     * Reasoning about what to do with seen item <br>
     * the easiest way of handeling it will be just to take it every time, but what should we do
     * when there are many of items laying in front of agent?
     * <ol>
     * <li> choose weapon - choose the type he is lacking (melee/ranged)
     * <li> choose armor
     * <li> choose health - if the health is bellow normal maximum
     * <li> choose ammo - if it is suitable for possessed weapons
     * <li> ignore the item
     * </ol>
     */

    private Item chooseItem() {
        if (this.memory.getSeeAnyReachableExtra()) {
            return this.memory.getSeeExtra();
        }

        // 1) choose weapon - choose the type he is lacking (melee/ranged)
        if (this.memory.getSeeAnyReachableWeapon()) {
            return chooseWeapon();
        }
        // 2) choose armor
        if (this.memory.getSeeAnyReachableArmor()) {
            return this.memory.getSeeReachableArmor();
        }
        // 3) choose health - if the health is bellow normal maximum or the item is boostable
        if (this.memory.getSeeAnyReachableHealth()) {
            Health health = this.memory.getSeeReachableHealth();
            if (this.memory.getAgentHealth() < 199) {
                return health;
            }
            if (health.boostable) // if the health item is boostable, grab it anyway:)
            {
                return health;
            }
        }
        // 4) choose ammo - if it is suitable for possessed weapons
        if ((this.memory.getSeeAnyReachableAmmo()) &&
                (this.memory.isAmmoSuitable(this.memory.getSeeReachableAmmo()))) {
            return this.memory.getSeeReachableAmmo();
        }
        // 5) ignore the item
        return null;
    }

    /**
     * sees reachable item and wants it
     * @return true if there is an item which is useful for agent
     */
    private boolean seeAnyReachableItemAndWantIt() {
        if (choosenItem == null) {
        if (this.memory.getSeeAnyReachableItem()) {
            choosenItem = chooseItem();
            if (choosenItem != null) {
                //this.log.info("NEW ITEM CHOSEN: " + choosenItem);
                //this.log.info("LAST CHOOSEN ITEM: " + previousChoosenItem);
            }
        } else {
            choosenItem = null;
        }
        }
        
        if ((choosenItem != null) && (!choosenItem.equals(previousChoosenItem))) //&& (Triple.distanceInSpace(memory.getAgentLocation(), choosenItem.location) > 20))
        {
            return true;
        } else {
            return false;
        }
    }


    /**
     * run along the path to choosen item
     */
    protected void stateSeeItem() {
        this.log.log(Level.INFO, "Decision is: SEE_ITEM --- Running for: " + this.choosenItem.toString());
        if (!this.gameMap.safeRunToLocation(choosenItem.location)) {         // unable to reach the choosen item
            log.info("unable to REACH the choosen item");
            previousChoosenItem = choosenItem;
            choosenItem = null;
        }
        this.jumped = false;
    }
       /**
     * checks whether there are any medkit items around and if there are
     * checks if the agent is not standing on the first one in the choosenMedKits
     * <p>
     * (bot got stucked because nearestHealth returns Healths according to inventory spots
     * not to the current situation, so the bot with low health got stucked on the inventory spot)
     * <p>
     * @return true if bot can run along med kits - initialize them before that
     */
    protected boolean canRunAlongMedKit() {
        if (this.choosenMedKits == null) {
            this.choosenMedKits = this.gameMap.nearestHealth(4, 8);
        }
        // no medkits to run to around the agent - restricted AStar - see nearestHealth
        if (choosenMedKits.isEmpty()) {
            this.choosenMedKits = null;
            return false;
        }
        // bot is too close to the object - possibly standing at the only one
        if (Triple.distanceInSpace(choosenMedKits.get(0).location, memory.getAgentLocation()) < 15) {
            // there are many - remove the first one - seeItem has highest priority, so bot should
            // pick up the item anyway and otherwise will not get stucked at the inventory spot of
            // the item
            if (choosenMedKits.size() > 2) {
                choosenMedKits.remove(0);
            } else {
                this.choosenItem = null;
                return false;
            }
        }
        return true;
    }

    protected boolean canRunAlongItems() {


        ArrayList<Weapon> weaponList = new ArrayList<Weapon>();
        ArrayList<Weapon> weaponListGood = new ArrayList<Weapon>();

        weaponList = memory.getKnownWeapons();

        for (Weapon thisWeapon : weaponList) {
            //log.info("thisWeapon:"+thisWeapon.getWeaponType().toString());
            //move on if we have this weapon already
            if (this.memory.hasWeaponOfType(thisWeapon.getWeaponType())) { continue; }
            log.info("thisWeapon:"+thisWeapon.getWeaponType().toString());
            double weaponTime = gameTime;
            boolean keyFlag = false;
            if (timerMap.containsKey(Integer.toString(thisWeapon.ID))) {
              keyFlag = true;
              //log.info("containsKey");
              Object lookup = timerMap.get(Integer.toString(thisWeapon.ID));
              weaponTime = Double.parseDouble(lookup.toString());
              //expired
              if (gameTime >= weaponTime+40) { timerMap.remove(Integer.toString(thisWeapon.ID)); keyFlag = false; }
            }
            else {
              //log.info("nokey");
              if (Triple.distanceInSpace(memory.getAgentLocation(),thisWeapon.location) < 100) {
              //log.info("addkey:"+gameTime+":"+thisWeapon.ID);
              timerMap.put(Integer.toString(thisWeapon.ID), new Double (Double.toString(gameTime)));
              }
            }

            //if (gameTime <= weaponTime+60) { continue; }
            if (keyFlag) { continue; }

            //log.info("gameTime:"+gameTime+":weaponTime:"+weaponTime);

            //move on if weapon not present at pickup
/*            if (Triple.distanceInSpace(memory.getAgentLocation(),thisWeapon.location) < 15) { continue; }

                  Object lookup = navMap.get(Integer.toString(navID));
      int navLookup = Integer.parseInt(lookup.toString());
      return navLookup;

  */

            //log.info("getWeapon:"+thisWeapon.getWeaponType().toString());
            weaponListGood.add(thisWeapon);
            //this.gameMap.safeRunToLocation(thisWeapon.location);
            //chosenNavigationPoint.location = thisWeapon.location;
            //return true;
        }

        //get closest available weapon
        //Weapon weaponGet = null;
        double minWeaponDistance = 30000.0;
        for (Weapon thisWeapon : weaponListGood) {
            double weaponDistance = Triple.distanceInSpace(memory.getAgentLocation(),thisWeapon.location);
            if (weaponDistance < minWeaponDistance) {
                minWeaponDistance = weaponDistance;
                weaponGet = thisWeapon;
            }
        }

        if (weaponGet != null) {
          log.info("getWeapon:"+weaponGet.getWeaponType().toString());
          chosenNavigationPoint = memory.getKnownNavPoints().get(0);
          chosenNavigationPoint.location = weaponGet.location;
          return true;
          /*if(this.gameMap.safeRunToLocation(weaponGet.location)) {
              if (Triple.distanceInSpace(memory.getAgentLocation(),weaponGet.location) < 50) {
                weaponGet = null;
              }
            return true;
           }
           */

        }

        return false;
    }
/*
        if (this.choosenItems == null) {
            this.choosenItems = this.gameMap.nearestItems(MessageType.WEAPON,3);
        }
        // no medkits to run to around the agent - restricted AStar - see nearestHealth
        if (choosenItems.isEmpty()) {
            this.choosenItems = null;
            return false;
        }
        // bot is too close to the object - possibly standing at the only one
        if (Triple.distanceInSpace(choosenItems.get(0).location, memory.getAgentLocation()) < 15) {
            // there are many - remove the first one - seeItem has highest priority, so bot should
            // pick up the item anyway and otherwise will not get stucked at the inventory spot of
            // the item
            if (choosenItems.size() > 2) {
                choosenItems.remove(0);
            } else {
                this.choosenItem = null;
                return false;
            }
        }
        return true;
 */
    
    protected boolean getReachableWeapons() {

        ArrayList<Weapon> weaponList = new ArrayList<Weapon>();
        ArrayList<Weapon> weaponListGood = new ArrayList<Weapon>();

        //log.info("debugGet2");
        weaponList = memory.getSeeReachableWeapons();
        if (weaponList.isEmpty()) { return false; }

        //log.info("debugGet1");
        for (Weapon thisWeapon : weaponList) {
            //move on if we have this weapon already
            if (this.memory.hasWeaponOfType(thisWeapon.getWeaponType())) { continue; }
            log.info("thisWeapon:"+thisWeapon.getWeaponType().toString());
            log.info("autotrace:"+this.memory.getAutoTrace(thisWeapon.ID));
            weaponListGood.add(thisWeapon);
        }

        //get closest available weapon
        //Weapon weaponGet = null;
        double minWeaponDistance = 30000.0;
        for (Weapon thisWeapon : weaponListGood) {
            double weaponDistance = Triple.distanceInSpace(memory.getAgentLocation(),thisWeapon.location);
            if (weaponDistance < minWeaponDistance) {
                minWeaponDistance = weaponDistance;
                weaponGetPickup = thisWeapon;
                //log.info("thisWeaponGet:"+weaponGet.getWeaponType().toString());
            }
        }

        if (weaponGetPickup != null) {
          //this.body.runToLocation(weaponGet.location);
          runType = "runto";

          log.info("getWeaponPickup:"+weaponGetPickup.getWeaponType().toString());
          chosenNavigationPoint = memory.getKnownNavPoints().get(0);
          chosenNavigationPoint.location = weaponGetPickup.location;
          return true;
          /*if(this.gameMap.safeRunToLocation(weaponGet.location)) {
              if (Triple.distanceInSpace(memory.getAgentLocation(),weaponGet.location) < 50) {
                weaponGet = null;
              }
            return true;
           }
           */
        }

        return false;
    }

    /**
     * runs along healths of strength at least 8 to recover health
     */
    protected void stateMedKit() {
        this.log.log(Level.INFO, "Decision is: RUN_MED_KITS:"+this.memory.getAgentHealth());
        this.gameMap.runAroundItemsInTheMap(choosenMedKits, this.useAStar);
    }

    protected void findItem() {
        this.log.log(Level.INFO, "Decision is: findItem");
        this.gameMap.runAroundItemsInTheMap(choosenItems, this.useAStar);
    }

   /*========================================================================*/

    /**
     * Computes the best location that should be used as strafing destination
     * while dancing around given enemy during a combat. Agent and enemy weapons
     * are considered to choose the location. Nearby health packs, vials and
     * armors are picked up along the way.
     *
     * <h4>Pogamut troubles</h4>
     *
     * How about using autotrace rays for scanning the ground around? Usage of
     * one autotrace ray pointed to the direction where the agent is aiming
     * might help prevent rocketry suicides. Well, it could, if the autotrace
     * were working as it was supposed to. For now, it causes more trouble than
     * it helps.
     *
     * <h4>Future</h4>
     *
     * What about foraging nearby healths? Check the perimeter and pick them up.
     * This could be done easily by comparing the calculated strafing vector
     * with the vectors of nearby reachable items. Should the angle between the
     * vectors be small enough, strafe to the item instead of the calculated
     * strafing point.
     *
     * <p>There is one pitfall to this however: The closer the items are to the
     * agent, the bigger might their angle-between-the-vectors be. Paradoxicaly:
     * the closer the item is, the more the angle starts to raise. And the vial
     * gets to be more attractive. In results, comparing the vectors only is
     * not good enough. Distance must be taken into consideration and tweaked
     * into a reasonable condition with the vectors angle.</p>
     *
     * @param enemy Enemy, which to dance around.
     * @return Strafing location to where to strafe to while wrestling.
     */
    protected Triple getStrafeAroundLocation (Player enemy)
    {
        // this is used for debugging purposes
        //if (main._DEBUGLocation != null) return main._DEBUGLocation;

        double desiredEnemyDistance = (this.random.nextFloat() * 400) + 150;
        double strafingAmount = (this.random.nextFloat() * 70) + 80;
        //double desiredEnemyDistance = 200;
        //double strafingAmount = 100;

        //random strafe direction change?
        int myRand = random.nextInt(3)+1;
        if (myRand == 1) { strafingRight = !strafingRight; }


/*        // primary fire mode
        if (!alternateFire)
        {
            desiredEnemyDistance = currentWeaponInfo.priIdealCombatRange;
            strafingAmount = currentWeaponInfo.priStrafingAmount;
        }
        // alternate fire mode
        else
        {
            desiredEnemyDistance = currentWeaponInfo.altIdealCombatRange;
            strafingAmount = currentWeaponInfo.altStrafingAmount;
        }
 */
        // get agent location from memory
        //Triple agentLocation = memory.self.getLocation ();
        Triple agentLocation = this.memory.getAgentLocation();

        // get location and velocity of enemy
        Triple enemyLocation = enemy.location;
        Triple enemyVelocity = enemy.velocity;

        // update the enemy location by its velocity
        enemyLocation = Triple.add(
            enemyLocation,
            //Triple.multiplyByNumber(enemyVelocity, 1/main.logicFrequency)
            Triple.multiplyByNumber(enemyVelocity, 1/5.0)
        );

        // compute planar direction to the enemy
        // howto: substract the two locations, remove z-axis, normalize
        Triple enemyDirection = Triple.subtract(enemyLocation, agentLocation);
        // remove z-axis
        enemyDirection.z = 0;
        // and normalize it
        enemyDirection = enemyDirection.normalize ();

        // compute distance to the enemy
        double enemyDistance = Triple.distanceInSpace(enemyLocation, agentLocation);

        // compute orthogonal direction to the enemy
        Triple enemyOrthogonal = new Triple (enemyDirection.y, -enemyDirection.x, 0);

        // decide, how much to move forward
        double moveForward = enemyDistance - desiredEnemyDistance;

        // decide, how much and where to strafe
        double moveStrafe = strafingRight ? strafingAmount : -strafingAmount;
               log.info("strafing:"+moveStrafe);
        // decide where to move..
        Triple moveDirection = moveDirection = Triple.add (
            // move forward/backward..
            Triple.multiplyByNumber (enemyDirection, moveForward),
            // and strafe to side along the way
            Triple.multiplyByNumber (enemyOrthogonal, moveStrafe)
        );

        // finally, add moving vector to current agent location
        return Triple.add(agentLocation, moveDirection);
    }

    @Override
    @SuppressWarnings("static-access")
    public void receiveMessage(RcvMsgEvent e) {
        // DO NOT DELETE! Otherwise things will screw up! Agent class itself is also using this listener...
        super.receiveMessage(e);

        if (e.getMessage().type.toString().equals("NAV_POINT")) { return; }
        if (e.getMessage().type.toString().equals("DELETE_FROM_BATCH")) { return; }
        if (e.getMessage().type.toString().equals("WEAPON")) { return; }
        if (e.getMessage().type.toString().equals("CHANGE_WEAPON")) { return; }
        if (e.getMessage().type.toString().equals("CHANGED_WEAPON")) { return; }
        if (e.getMessage().type.toString().equals("ITEM")) { return; }
        if (e.getMessage().type.toString().equals("AMMO")) { return; }
        if (e.getMessage().type.toString().equals("MOVER")) { return; }
        //if (e.getMessage().type.toString().equals("BEGIN")) { return; }
        if (e.getMessage().type.toString().equals("END")) { return; }
        if (e.getMessage().type.toString().equals("GAME_STATUS")) { return; }
        if (e.getMessage().type.toString().equals("SELF")) { return; }
        if (e.getMessage().type.toString().equals("HEALTH")) { return; }
        if (e.getMessage().type.toString().equals("ADD_ITEM")) { return; }
        if (e.getMessage().type.toString().equals("ADD_AMMO")) { return; }
        if (e.getMessage().type.toString().equals("ADD_HEALTH")) { return; }
        if (e.getMessage().type.toString().equals("ADD_SPECIAL")) { return; }
        if (e.getMessage().type.toString().equals("ADD_WEAPON")) { return; }
        if (e.getMessage().type.toString().equals("ADRENALINE_GAINED")) { return; }
        if (e.getMessage().type.toString().equals("ARMOR")) { return; }
        if (e.getMessage().type.toString().equals("SPECIAL")) { return; }
        if (e.getMessage().type.toString().equals("HEAR_NOISE")) { return; }
        if (e.getMessage().type.toString().equals("HEAR_PICKUP")) { return; }
        if (e.getMessage().type.toString().equals("PLAYER")) { return; }
        if (e.getMessage().type.toString().equals("PATH")) { return; }

        if (!(e.getMessage().type.toString().equals("BEGIN"))) {
            getLogger().info("message: " + e.getMessage().type.toString());
        }
        // Take care of frags and deaths.
        switch (e.getMessage().type) {
            case PLAYER_KILLED:
              PlayerKilled pk;

              pk = (PlayerKilled) e.getMessage();
              if (pk.killerID == getMemory().getAgentID()) {
                frags += 1;
                //getLogger().info("pk: "+pk.killerID+":"+lastEnemy.ID+":"+pk.ID+":"+this.memory.getAgentID());
                try { dbWrite(memory.getGameInfo().level.toString(),chosenNavigationPoint.getID(),memory.getKnownNavPoints().indexOf(chosenNavigationPoint),chosenNavigationPoint.location.toString(),chosenNavigationPoint.UnrealID.toString(),memory.getAgentLocation().toString(),gameTime, 1); } catch (Exception K) { System.out.println("Hello World!"); }
              }

              //frags += 1;

              //to correct bot pursuing after dead enemy disappears
              if (pk.killerID == this.memory.getAgentID()) {
                //log.info("got'EM");
                lastEnemy = null;
              }

              break;
            case BOT_KILLED:
              BotKilled bk;

              bk = (BotKilled) e.getMessage();
              //if (bk.killerID == getMemory().getAgentID()) {
                deaths += 1;
                getLogger().info("bk: " + bk.killerID);
                //getLogger().info("location:"+memory.getAgentLocation().toString());
                //probably should avoid places killed, but trying returning to the scene of the crime for now
                try { dbWrite(memory.getGameInfo().level.toString(),chosenNavigationPoint.getID(),memory.getKnownNavPoints().indexOf(chosenNavigationPoint),chosenNavigationPoint.location.toString(),chosenNavigationPoint.UnrealID.toString(),memory.getAgentLocation().toString(),gameTime, 1); } catch (Exception K) { System.out.println("Hello World!"); }
              //}

              //deaths += 1;
              break;
            case GLOBAL_CHAT:
              GlobalChat gc;

              gc = (GlobalChat) e.getMessage();
              getLogger().info("Message: " + gc.string);
              break;
            case BEGIN:
              BeginMessage begin;

              begin = (BeginMessage) e.getMessage();
              //getLogger().info("Message: " + begin.time);
              gameTime = begin.time;
              break;
            case WALL_COLLISION:
              strafingRight = !strafingRight;
              int myWCRand = random.nextInt(3)+1;
              if (myWCRand == 1) { this.body.jump(); }
              if (myWCRand == 2) { this.body.doubleJump(); }
              break;
            case INCOMMING_PROJECTILE:
  
                int myRand = random.nextInt(5)+1;

            int myRandSleep = random.nextInt(100);
            try {
                Thread.currentThread().sleep(200+myRandSleep);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
                
                if (myRand == 1) {
                  log.info("dodge right");
                  Triple dodgeDirection = new Triple (0.0,1.0,0.0);
                  this.body.dodge(dodgeDirection);
                }
                if (myRand == 2) {
                  log.info("dodge left");
                  Triple dodgeDirection = new Triple (0.0,-1.0,0.0);
                  this.body.dodge(dodgeDirection);
                }
                if (myRand == 3) {
                  log.info("jump");
                  this.body.jump();
                }

/*
        if (turnTime == 0.0) {
            turnTime = gameTime;
        }
        double waitTimeHit = 0.0;
        double maxEffectTime = 5.0;
        //only perform turn if within last waitTimeHit seconds

        log.info(gameTime+":"+turnTime);
        if ((gameTime >= turnTime+waitTimeHit) && (gameTime <= turnTime+maxEffectTime)) {
            log.info("INCOMMING register");
            //this.body.turnHorizontal(180);
                int myRand = random.nextInt(3)+1;

                if (myRand == 1) {
                  log.info("dodge right");
                  Triple dodgeDirection = new Triple (0.0,1.0,0.0);
                  this.body.dodge(dodgeDirection);
                }
                if (myRand == 2) {
                  log.info("dodge left");
                  Triple dodgeDirection = new Triple (0.0,-1.0,0.0);
                  this.body.dodge(dodgeDirection);
                }
                if (myRand == 3) {
                  log.info("jump");
                  this.body.jump();
                }
        }
        else {
            log.info("INCOMMING ignored");
            if (gameTime > turnTime+maxEffectTime) {
              log.info("turntime reset");
              turnTime = 0.0;
            }
        }
*/

              break;
            case SPAWN:
              runType = null;
              weaponGetPickup = null;
              weaponGet = null;
              break;
        }
    }


    /**
     * NOTE: this method MUST REMAIN DEFINED + MUST REMAIN EMPTY, due to technical reasons.
     */
    public static void main(String[] Args) {
    }

  public void dbWrite(String map_level, int nav_ID, int ID, String location, String UnrealID, String eventLocation, double eventTime, int eventWeight) throws Exception {

      Class.forName(sqliteClass);
      Connection conn = DriverManager.getConnection(sqliteDBPath);
      Statement stat = conn.createStatement();
      //stat.executeUpdate("drop table if exists obs;");
      //stat.executeUpdate("create table people (name, occupation);");
      PreparedStatement prep = conn.prepareStatement(
          "insert into obs(row_entry_date,map_level,map_id,navpoint_id,location,unreal_id,event_location,event_time,event_weight) values (datetime('now'),?,?,?,?,?,?,?,?);");

      log.fine("navpoint_ID="+ID);

      prep.setString(1, map_level);
      prep.setInt(2, nav_ID);
      prep.setInt(3, ID);
      prep.setString(4, location);
      prep.setString(5, UnrealID);
      prep.setString(6, eventLocation);
      prep.setDouble(7, eventTime);
      prep.setDouble(8, eventWeight);

      prep.addBatch();

      conn.setAutoCommit(false);
      prep.executeBatch();
      conn.setAutoCommit(true);

      conn.close();
  }

  public void dbWriteNavpoint(String map_level, int from_navpoint_id, int to_navpoint_id) throws Exception {

      Class.forName(sqliteClass);
      Connection conn = DriverManager.getConnection(sqliteDBPath);
      Statement stat = conn.createStatement();

      log.info("dbWriteNavpoint:"+map_level+":"+from_navpoint_id+":"+to_navpoint_id);

      PreparedStatement prep = conn.prepareStatement(
          "insert into navpoint(row_entry_date,map_level,from_navpoint_id,to_navpoint_id,visibility) values (datetime('now'),?,?,?,1);");

      prep.setString(1, map_level);
      prep.setInt(2, from_navpoint_id);
      prep.setInt(3, to_navpoint_id);

      prep.addBatch();

      conn.setAutoCommit(false);
      prep.executeBatch();
      conn.setAutoCommit(true);

/*
      String sql = "insert into navpoint(row_entry_date,map_level,from_navpoint_id,to_navpoint_id,visibility) values (datetime('now'),'"+map_level+"',"+from_navpoint_id+","+to_navpoint_id+",1);";
      log.info("sql:"+sql);

      conn.setAutoCommit(false);
      stat.executeQuery(sql);
      conn.setAutoCommit(true);
 */

      conn.close();
  }

public boolean dbLoggedNavpoint(String map_level,int from_navpoint_id) throws Exception {

      //checks to see if this navpoint was logged on the database earlier or not

      Class.forName(sqliteClass);
      Connection conn = DriverManager.getConnection(sqliteDBPath);
      Statement stat = conn.createStatement();

      String sql = null;
      sql = "select from_navpoint_id from navpoint where from_navpoint_id = "+from_navpoint_id+" and map_level = '"+map_level+"' limit 1;";

      log.info("sql:"+sql);
      ResultSet rs = stat.executeQuery(sql);

      boolean flagFound = false;
      while (rs.next()) {
             flagFound = true;
      }

      rs.close();
      conn.close();

      return flagFound;

      }

public void setNavpoint(String behavior) throws Exception {

      //set an initial location default if null
      if (lastNavigationPoint == null) {
        log.info("navpoint=null");
        lastNavigationPoint = memory.getKnownNavPoints().get(0);
        chosenNavigationPoint = memory.getKnownNavPoints().get(0);
      }

      Class.forName(sqliteClass);
      Connection conn = DriverManager.getConnection(sqliteDBPath);
      Statement stat = conn.createStatement();
      String sql = null;

      int myRand = random.nextInt(3)+1;
      //log.info("myRand = " + myRand);

      String map_level = memory.getGameInfo().level;
      //use locations within past # seconds, else randomSearch
      double recentTime = gameTime - 120;
      if (recentTime < 0) { recentTime = 0; }

      if (behavior.equals("normal")) {
        sql = "select navpoint_id,event_location from obs where row_entry_date > strftime('%Y-%m-%d %H:%M:%S','now','-2 minute') and event_time > "+recentTime+" and event_weight = 1 and map_level = '"+map_level+"' order by row_entry_date desc limit 3;";
      }
      else if (behavior.equals("camp")) {
         log.info("camp");
         sql = "select navpoint_id,event_location from obs where event_weight = 2 and map_level = '"+map_level+"' order by row_entry_date desc limit 3;";
      }
      else if (behavior.equals("hide")) {
         //log.info("hide");

         //default hide from last position if no enemy
         if (hideFromNav == -1 || enemy == null) { hideFromNav = knownNavLkp(lastNavigationPoint.ID); }

         //get a fix on the latest enemy position if available
         //log.info("debug0:"+this.memory.getSeeAnyEnemy()+":"+this.memory.getSeeAnyPlayer());
         if (this.memory.getSeeAnyEnemy() || this.memory.getSeeAnyPlayer()) {

             this.lastEnemy = enemy;
             //this.enemy = this.memory.getSeePlayer(this.enemy.ID);
             if (this.memory.getSeeAnyPlayer()) {
               this.enemy = this.memory.getSeePlayer();
             }
             if (this.memory.getSeeAnyEnemy()) {
               this.enemy = this.memory.getSeeEnemy();
             }

           hideFromNav = findClosestNavpoint(enemy.location);
           log.info("enemy:"+enemy.name.toString()+":"+memory.getKnownNavPoints().get(hideFromNav).UnrealID.toString());
         }

         sql = "select to_navpoint_id from navpoint where map_level = '"+map_level+"' and from_navpoint_id = "+memory.getKnownNavPoints().get(hideFromNav).ID+";";

         log.info("sql:"+sql);
         ResultSet rs = stat.executeQuery(sql);

         //tried but failed to assign using getArray
         //http://publib.boulder.ibm.com/infocenter/idshelp/v10/index.jsp?topic=/com.ibm.jdbc_pg.doc/jdbc126.htm
         //http://forums.sun.com/thread.jspa?threadID=523675

         //rs.next();

         ArrayList<Integer> ArrayToGet = new ArrayList<Integer> ();
         while (rs.next()) {
             //log.info("visList:"+rs.getInt(1));
             ArrayToGet.add(rs.getInt(1));
         }
         //ArrayToGet = (ArrayList<Integer>) rs.getArray(1)
         //for (int j=0; j<ArrayToGet.size(); j++) {
           //log.info("integer element = "+ArrayToGet.get(j).toString());
         //}

         //Object testVal = navMap.get("1");
         //log.info("testval:"+testVal.toString());

         //get navpoints from visible set
         //int myRandHide = random.nextInt(ArrayToGet.size());
         //int thisNavpoint = ArrayToGet.get(myRandHide);

         //get navpoints from non-visible set
         int hideNavFound = 0;
         int thisNavpoint = 0;

         if (!ArrayToGet.isEmpty()) {

           while (hideNavFound == 0) {
             int myRandHide = random.nextInt(memory.getKnownNavPoints().size());
             thisNavpoint = memory.getKnownNavPoints().get(myRandHide).ID;
             //log.info("thisNavpoint:"+thisNavpoint);
             if (ArrayToGet.contains(thisNavpoint)) {
               //log.info("navpoint is visible:"+memory.getKnownNavPoints().get(myRandHide).UnrealID.toString());
             }
             else { log.info("navpoint not visible:"+memory.getKnownNavPoints().get(myRandHide).UnrealID.toString()); hideNavFound = 1; }
           }
         }
         else {
           int myRandHide = random.nextInt(memory.getKnownNavPoints().size());
           thisNavpoint = memory.getKnownNavPoints().get(myRandHide).ID; 
         }

         //log.info("hide_navpoint:"+thisNavpoint);
         int intLookup = knownNavLkp(thisNavpoint);
         log.info("hide_navpoint:"+thisNavpoint+":"+intLookup);
         chosenNavigationPoint = memory.getKnownNavPoints().get(intLookup);

         //for (int i=0; i<memory.getKnownNavPoints().size(); i++) {
         //for (int i=0; i<10; i++) {
             //log.info(i+":"+memory.getKnownNavPoints().get(i).ID);
            //if (memory.getKnownNavPoints().get(i).ID == thisNavpoint) {
              //log.info("hide_navpoint:"+thisNavpoint+":"+i);
              //chosenNavigationPoint = memory.getKnownNavPoints().get(i);
            //}
         //}
        //}

      rs.close();
      conn.close();

      return;

      }
      else if (behavior.equals("navlog")) {

          if ((botYFocus == 4) || (lastNavigationPoint == null)) {

            botYFocus = 0;

            int navSize = memory.getKnownNavPoints().size();
            //int navSize = 130;

            if (navCount >= navSize) { navCount = 0; }  //navCount = 128;

            chosenNavigationPoint = memory.getKnownNavPoints().get(navCount);
            log.info("navpoint("+navCount+"/"+navSize+"):"+chosenNavigationPoint.UnrealID);

            navCount++;
            //skip catalogging jumppads since we can't sit on them to inventory
            while (memory.getKnownNavPoints().get(navCount).UnrealID.contains("JumpPad")) { navCount++; }
            //while (dbLoggedNavpoint(memory.getGameInfo().level.toString(),memory.getKnownNavPoints().get(navCount).ID)) { log.info("skipping navpoint:"+navCount); navCount++; }
            return;
          }
          else {

            chosenNavigationPoint = lastNavigationPoint;
            double botY = this.memory.getAgentRotation().y;

            if (botYFocus == 0) { botYMin = 0.0; botYMax = 2000.0; }
            if (botYFocus == 1) { botYMin = 16000.0; botYMax = 18000.0; }
            if (botYFocus == 2) { botYMin = 32000.0; botYMax = 34000.0; }
            if (botYFocus == 3) { botYMin = 48000.0; botYMax = 50000.0; }

            if ((botY < botYMin) || (botY > botYMax)) {
               this.body.turnHorizontal(5);
               return;
            }
            else {
              log.info("rotation:"+this.memory.getAgentRotation().y);
              ArrayList<NavPoint> testNavigationPoint = memory.getSeeNavPoints();
              for (int i=0; i<=testNavigationPoint.size()-1; i++) {
                if (testNavigationPoint.get(i).type.toString().equals("NAV_POINT")) {
                   log.info("navVis("+botYFocus+"/"+i+")"+testNavigationPoint.get(i).UnrealID);
                   //log.info(chosenNavigationPoint.getID()+":"+testNavigationPoint.get(i).getID());
                   try { dbWriteNavpoint(memory.getGameInfo().level.toString(),chosenNavigationPoint.getID(),testNavigationPoint.get(i).getID()); } catch (Exception K) { System.out.println("Hello World!"); }
                }
              }
            }
          
            botYFocus++;

            return;
          }
        }
      //}

      log.info("sql:"+sql);
      ResultSet rs = stat.executeQuery(sql);

      log.info("myRand = " + myRand);
      String thisLocation = null;

      int i = 1;
      while (rs.next()) {
        //log.info("i:"+i);
        if (i == myRand) {
          //log.info("found_row:"+i);
          thisLocation = rs.getString("event_location");
          //below line required or bot errors out
          chosenNavigationPoint = memory.getKnownNavPoints().get(rs.getInt("navpoint_id"));
        }
        i++;
      }
      //}
      rs.close();
      conn.close();

      //default random, if database not populated
      if (thisLocation == null) {
          chosenNavigationPoint = memory.getKnownNavPoints().get(random.nextInt(memory.getKnownNavPoints().size()));
          log.info("randomLocation:"+chosenNavigationPoint.UnrealID.toString());
      }
      else {

          log.info("thisLocation=" + thisLocation);

          String[] temp = null;
          temp = thisLocation.split(",");

          chosenNavigationPoint.location.x = Double.valueOf(temp[0]);
          chosenNavigationPoint.location.y = Double.valueOf(temp[1]);
          chosenNavigationPoint.location.z = Double.valueOf(temp[2]);
      //chosenNavigationPoint.location.x = 698.0;
      //chosenNavigationPoint.location.y = -94.0;
      //chosenNavigationPoint.location.z = -111.0;
      }

  }

/* public int findClosestNavpoint(Triple thisLocation) {
 * finds closest getKnownNavPoints navpoint_id to a given location
 * can probably replace with gamemap.nearestnav
 */

public int findClosestNavpoint(Triple thisLocation) {

       double minDistance = 10000.00; //should be larger than most node internode distances
       int minNav = 0;
       for (int i=0; i<memory.getKnownNavPoints().size(); i++) {
         double navDistance = Triple.distanceInSpace(thisLocation, memory.getKnownNavPoints().get(i).location);
         if (navDistance < minDistance) { minDistance = navDistance; minNav = i; }
         //log.info("enemy:"+enemy.name.toString()+":"+navDistance+":"+memory.getKnownNavPoints().get(i).UnrealID.toString());
       }
       //log.info("minDistance:"+minDistance+":"+memory.getKnownNavPoints().get(minNav).UnrealID.toString());
      return minNav;

}

public int knownNavLkp(int navID) {
      Object lookup = navMap.get(Integer.toString(navID));
      int navLookup = Integer.parseInt(lookup.toString());
      return navLookup;
}

}


