package com.jason.breakout;

/**
 * Created by JasonWong on 07-Nov-17.
 */
public class Algorithm {
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.02;
    private static final int tournamentSize = 50;
    private static final boolean elitism = true;
    public Population evolvePopulation(Population pop) {
        Population newPopulation =  new Population(pop.players.length,false);
        // Keep our best individual
        if(elitism){
            newPopulation.players[0] = pop.getFittest();
        }
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }

        //cross over
        for (int i = elitismOffset; i < pop.players.length; i++) {
            Player indiv1 = tournamentSelection(pop);
            Player indiv2 = tournamentSelection(pop);
            Player newIndiv = crossover(indiv1, indiv2);
            newPopulation.players[i] = newIndiv;
        }

        for (int i = elitismOffset; i < newPopulation.players.length; i++) {
            mutate(newPopulation.players[i]);
        }
        newPopulation.players[0].fitness = 0;
        return newPopulation;
    }

    private Player tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.players.length);
            tournament.players[i] = pop.players[randomId];
        }
        // Get the fittest
        Player fittest = tournament.getFittest();
        return fittest;
    }

    private Player crossover(Player indiv1, Player indiv2) {
        Player newSol = new Player();
        // Loop through genes
        for (int i = 0; i < indiv1.gene.length; i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.gene[i] =indiv1.gene[i];
            } else {
                newSol.gene[i] =indiv2.gene[i];
            }
        }
        return newSol;
    }

    private void mutate(Player indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.gene.length; i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                indiv.gene[i] = ((float) Math.random()*20) -10;
            }
        }
    }
}

class Population {
    Player[] players;

    Population (int size,boolean init){
        players = new Player[size];
        if(init){
            for(int i = 0;i<players.length;i++){
                Player p = new Player();
                players[i] = p;
            }

        }

    }
    public Player getFittest() {
        Player fittest = players[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < players.length; i++) {
            if (fittest.fitness <= players[i].fitness) {
                fittest = players[i];
            }
        }
        return fittest;
    }
}

class Player {
    static int geneLength = 74;
    float[] gene = new float[geneLength];
    int fitness;

    public Player(){
        for (int i = 0; i < geneLength; i++) {
            gene[i] = (float)Math.random();
        }
    }

}
