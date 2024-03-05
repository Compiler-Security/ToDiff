/*
 * This pass apply one Rule to OpAG
 * Input
 * OpAG
 * Rule
 *
 * Output
 * OPAG'
 *
 * The algorithm:
 * 1. Input Rule is the root node of Topological rule graph
 * 2. Before apply one Rule, we must meet preRules of this rule
 * We can demonstrate that this rule forms a topological graph without circle
 * It's worth to mention that when we apply one Rule, other rules in the graph may be applied as well
 * 3. So every time we apply from root node use dfs or bfs by postorder, we apply the rule of this node
 * if and only if all the preRule of this node is meet, we build rule graph at the same time
 * 4. We dynamically construct topological rule graph when applying rules
 * That is:
 * solve rules use rule graph <-> build rule graph by solving rules
 *
 * 5. We can demonstrate that the max num of rules of the rule graph is limited, so the algorithm always terminates
 */
package org.generator.lib.generator.pass;

public class applyRulePass {
}
