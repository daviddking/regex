package ddk.regex;

import java.util.ArrayList;
import java.util.List;

public class Pattern {

    // Meta-characters
    private static final int ANY = 256;
    private static final int EPSILON = 257;

    private final State startingState = new State();

    public Pattern(String patternString) {
        State currentState = startingState;
        for (char patternChar : patternString.toCharArray()) {
            State nextState = new State();
            switch (patternChar) {
                case '?':
                    currentState.addEdge(ANY, nextState);
                    break;
                case '*':
                    currentState.addEdge(EPSILON, nextState);
                    nextState.addEdge(ANY, nextState);
                    break;
                default:
                    currentState.addEdge(patternChar, nextState);
            }
            currentState = nextState;
        }
        currentState.isMatch = true;

        // Any trailing input after a match will be consumed
        // by the trailing input node. Since trailing input is
        // considered a non-match this is not a matching node
        State trailingInput = new State();
        currentState.addEdge(ANY, trailingInput);
        trailingInput.addEdge(ANY, trailingInput);
    }

    public boolean matches(String input) {
        List<State> currentStates = new ArrayList<>();
        List<State> nextStates = new ArrayList<>();
        List<State> tempStates;
        currentStates.add(startingState);

        for (char inputChar : input.toCharArray()) {
            for (State state : currentStates) {
                addNextStates(state, inputChar, nextStates);
            }
            // swap nextStates and currentStates
            tempStates = currentStates;
            currentStates = nextStates;
            nextStates = tempStates;
            nextStates.clear();
        }

        return anyMatching(currentStates);
    }

    private boolean anyMatching(List<State> states) {
        for (State state : states) {
            if (state.isMatch) {
                return true;
            }
        }
        return false;
    }

    private void addNextStates(State state, char inputChar, List<State> nextStates) {
        for (Edge edge : state.edges) {
            switch (edge.matchingCharacter) {
                case EPSILON:
                    addNextStates(edge.nextState, inputChar, nextStates);
                    break;
                case ANY:
                    nextStates.add(edge.nextState);
                    break;
                default:
                    if (edge.matchingCharacter == (int) inputChar) {
                        nextStates.add(edge.nextState);
                    }
            }
        }
    }

    private static class State {
        private final List<Edge> edges = new ArrayList<>(2);
        private boolean isMatch;

        void addEdge(int matchingChar, State nextState) {
            edges.add(new Edge(matchingChar, nextState));
        }

    }

    private static class Edge {

        private final int matchingCharacter;
        private final State nextState;

        Edge(int matchingCharacter, State nextState) {
            this.matchingCharacter = matchingCharacter;
            this.nextState = nextState;
        }
    }

}
