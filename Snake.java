import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A Snake is a predator that will eat both foxes and rabbits
 * they do not live as long as foxes, but will breed less,
 * and are less likely to starve.
 *
 * @author Mihail Vaporakis
 * @version 11-22-2021
 */
public class Snake extends Animal
{
    // Characteristics shared by all Snakes (class variables).

    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a snake can live.
    private static final int MAX_AGE = 120;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.02;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 13;
    //The food value of a single fox
    private static final int FOX_FOOD_VLAUE = 14;
    // The probablity that a snake will eat a fox
    private static final double FOX_EAT_CHANCE = 0.15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The snake's food level, which is increased by eating rabbits or foxes.
    private int foodLevel;

    /**
     * Create a snake. A snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            setAge(0);
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }

    /**
     * This is what the snake does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSnakes A list to return newly born snakes.
     */
    public void act(List<Animal> newSnakes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSnakes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this snake more hungry. This could result in the snake's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);

            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    return where;
                }
            }

            if(animal instanceof Fox) {

                if(rand.nextDouble() <= FOX_EAT_CHANCE)
                {
                    Fox fox = (Fox) animal;
                    if(fox.isAlive()) {
                        fox.setDead();
                        foodLevel = FOX_FOOD_VLAUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSnakes A list to return newly born snakes.
     */
    private void giveBirth(List<Animal> newSnakes)
    {
        // New snakes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Snake young = new Snake(false, field, loc);
            newSnakes.add(young);
        }
    }

    /**
     * @return the breeding age of this animal
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * @return the maxium age for this animal
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * @return the breeding probability
     */
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * @return the maximum litter size
     */
    public int getMaxLitter()
    {
        return MAX_LITTER_SIZE;
    }
}