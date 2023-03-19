Model Verifier in Kotlin.

This is a draft formal model verifer written in Kotlin and used in Kotlin
that can be used to define computational models with a state, with "steps"
acting moving states to other states, and invariants that the model is
supposed to uphold.

Solving a model does a simple search in the state space, respecting any
defined constraints, and outputs a state reachable form the defined
initial state that doesn't satisfy the invariants, if such a state is
found.

The verifier can be used - in theory - to find errors in parallel
algorithms. Currently, it can solve a few basic examples, but is missing
a lot including any non-trivial feature and any optimization.

This is not an officially supported Google product.
