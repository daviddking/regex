package ddk.regex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Pattern {

    // Meta-characters
    private static final int ANY_CHARACTER = 256;
    private static final int EMPTY = 257;

    private final State startingState = new State(0);

    public Pattern(String patternString) {
        int stateId = 1;
        State currentState = startingState;
        for (char patternChar : patternString.toCharArray()) {
            State nextState = new State(stateId++);
            switch (patternChar) {
                case '?':
                    currentState.addEdge(ANY_CHARACTER, nextState);
                    break;
                case '*':
                    currentState.addEdge(EMPTY, nextState);
                    nextState.addEdge(ANY_CHARACTER, nextState);
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
        State trailingInput = new State(stateId);
        currentState.addEdge(ANY_CHARACTER, trailingInput);
        trailingInput.addEdge(ANY_CHARACTER, trailingInput);
    }

    public boolean matches(String input) {
        Set<State> currentStates = new HashSet<>();
        Set<State> nextStates = new HashSet<>();
        Set<State> tempStates;
        currentStates.add(startingState);

        for (char inputChar : input.toCharArray()) {
            for (State state : currentStates) {
                addNextStates(state, inputChar, nextStates);
            }

            // Short-circuit search if nextStates is empty
            // since at that point there can never be a match
            if (nextStates.isEmpty()) {
                return false;
            }

            // Swap nextStates and currentStates
            tempStates = currentStates;
            currentStates = nextStates;
            nextStates = tempStates;
            nextStates.clear();
        }

        return anyMatching(currentStates);
    }

    private boolean anyMatching(Set<State> states) {
        for (State state : states) {
            if (state.isMatch) {
                return true;
            }
        }
        return false;
    }

    private void addNextStates(State state, char inputChar, Set<State> nextStates) {
        for (Edge edge : state.edges) {
            switch (edge.matchingCharacter) {
                case EMPTY:
                    addNextStates(edge.nextState, inputChar, nextStates);
                    break;
                case ANY_CHARACTER:
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
        private final int stateId;
        private final List<Edge> edges = new ArrayList<>(2);
        private boolean isMatch;

        State(int stateId) {
            this.stateId = stateId;
        }

        void addEdge(int matchingChar, State nextState) {
            edges.add(new Edge(matchingChar, nextState));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return stateId == state.stateId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stateId);
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
