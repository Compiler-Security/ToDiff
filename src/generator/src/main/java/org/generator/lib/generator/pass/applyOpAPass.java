/*
 * This pass will apply OpA to gen new OpAG
 * it will insert OpA at random place to OpAG to ensure that
 * Reduce(OpAG + OpA) = Reduce(OpAG apply OpA)
 * `+` means add OpA in the end of the OpAG
 *
 * FIXME for simplicity currently we can only add opAs in the end of OpAG
 */
package org.generator.lib.generator.pass;

public class applyOpAPass {
}
