/*
 * MIT License
 *
 * Copyright (c) 2024 HideTheMonkey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hidethemonkey.pathinator.helpers;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class SegmentData {
    private Material baseMaterial;
    private Material clearanceMaterial;
    private Location baseLocation;
    private World world;
    private int clearance;
    private Location lightingLocation;
    private ArrayList<ItemStack> lightingItems;
    private BlockFace baseFacing;
    private BlockFace lightFacing;
    private boolean useLighting = false;

    /**
     * Default constructor for SegmentData.
     * Sets the clearanceMaterial to AIR.
     */
    public SegmentData() {
        clearanceMaterial = Material.AIR; // default to AIR
    }

    /**
     * Gets the base material of the segment.
     *
     * @return the base material
     */
    public Material getBaseMaterial() {
        return baseMaterial;
    }

    /**
     * Sets the base material of the segment.
     *
     * @param baseMaterial the new base material
     */
    public void setBaseMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
    }

    /**
     * Gets the clearance material of the segment.
     *
     * @return the clearance material
     */
    public Material getClearanceMaterial() {
        return clearanceMaterial;
    }

    /**
     * Gets the clearance of the segment.
     *
     * @return the clearance
     */
    public int getClearance() {
        return clearance;
    }

    /**
     * Sets the clearance of the segment.
     *
     * @param clearance the new clearance
     */
    public void setClearance(int clearance) {
        this.clearance = clearance;
    }

    /**
     * Sets the clearance material of the segment.
     *
     * @param airMaterial the new clearance material
     */
    public void setClearanceMaterial(Material airMaterial) {
        this.clearanceMaterial = airMaterial;
    }

    /**
     * Gets the base location of the segment.
     *
     * @return the base location
     */
    public Location getBaseLocation() {
        return baseLocation;
    }

    /**
     * Sets the base location of the segment.
     *
     * @param baseLocation the new base location
     */
    public void setBaseLocation(Location baseLocation) {
        this.baseLocation = baseLocation;
    }

    /**
     * Gets the world of the segment.
     *
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Sets the world of the segment.
     *
     * @param world the new world
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Gets the base facing direction of the segment.
     *
     * @return the base facing direction
     */
    public BlockFace getBaseFacing() {
        return baseFacing;
    }

    /**
     * Sets the base facing direction of the segment and also sets the light facing
     * direction.
     *
     * @param facing the new base facing direction
     */
    public void setBaseFacing(BlockFace facing) {
        this.baseFacing = facing;
        this.lightFacing = BlockHelper.rotate90(facing);
    }

    /**
     * Gets the light facing direction of the segment.
     *
     * @return the light facing direction
     */
    public BlockFace getLightFacing() {
        return lightFacing;
    }

    /**
     * Sets the lighting stacks of the segment.
     *
     * @param lightingStacks the new lighting stacks
     */
    public void addLightingStacks(ArrayList<ItemStack> lightingStacks) {
        this.lightingItems = lightingStacks;
    }

    /**
     * Gets the lighting stacks of the segment.
     *
     * @return the lighting stacks
     */
    public ArrayList<ItemStack> getLightingStacks() {
        return lightingItems;
    }

    /**
     * Since this function depends on baseLocation, it is important to
     * only call this after setting baseLocation with setBaseLocation()
     * and lightFacing in setBaseFacing()
     */
    public void addLighting() {
        useLighting = true;
        lightingLocation = baseLocation.clone();
        lightingLocation.add(lightFacing.getModX(), lightFacing.getModY(), lightFacing.getModZ());
    }

    /**
     * Clears the lighting of the segment.
     */
    public void clearLighting() {
        useLighting = false;
        lightingLocation = null;
    }

    /**
     * Gets the lighting location of the segment.
     *
     * @return the lighting location
     */
    public Location getLightingLocation() {
        return lightingLocation;
    }

    /**
     * Checks if the segment uses lighting.
     *
     * @return true if the segment uses lighting, false otherwise
     */
    public boolean getUseLighting() {
        return useLighting;
    }
}