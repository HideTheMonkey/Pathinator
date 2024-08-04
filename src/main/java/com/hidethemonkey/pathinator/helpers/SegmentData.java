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
import org.bukkit.util.BoundingBox;

public class SegmentData {
    private Material baseMaterial;
    private Material clearanceMaterial;
    private Location baseLocation;
    private World world;
    private int clearance;
    private BlockFace baseFacing;
    private ArrayList<Material> rightMaterials;
    private ArrayList<Material> leftMaterials;
    private Section currentSection;
    private int sideIndex;
    // BoundingBox that represents where no blocks should be placed
    private BoundingBox negativeSpace;
    private BoundingBox emptyBox = new BoundingBox(0, 0, 0, 0, 0, 0);
    // Lighting
    private BlockFace lightFacing;
    private Location lightingLocation;
    private ArrayList<ItemStack> lightingItems;
    private boolean useLighting = false;
    // Power Rails
    private BlockFace powerFacing;
    private boolean usePower = false;
    private Location powerLocation;
    // Rails
    private boolean useRails = false;

    public enum Section {
        CENTER, LEFT, RIGHT
    }

    /**
     * Default constructor for SegmentData.
     * Sets the clearanceMaterial to AIR.
     */
    public SegmentData() {
        clearanceMaterial = Material.AIR; // default to AIR
        currentSection = Section.CENTER;
    }

    public SegmentData(SegmentData data) {
        this.baseFacing = data.getBaseFacing();
        this.baseLocation = data.getBaseLocation();
        this.baseMaterial = data.getBaseMaterial();
        this.clearance = data.getClearance();
        this.clearanceMaterial = data.getClearanceMaterial();
        this.currentSection = data.getCurrentSection();
        this.leftMaterials = data.getLeftMaterials();
        this.lightFacing = data.getLightFacing();
        this.lightingItems = data.getLightingStacks();
        this.lightingLocation = data.getLightingLocation();
        this.negativeSpace = data.getNegativeSpace();
        this.powerFacing = data.getPowerFacing();
        this.powerLocation = data.getPowerLocation();
        this.rightMaterials = data.getRightMaterials();
        this.sideIndex = data.getSideIndex();
        this.useLighting = data.getUseLighting();
        this.usePower = data.getUsePower();
        this.useRails = data.getUseRails();
        this.world = data.getWorld();
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
     * Checks if the segment uses rails.
     *
     * @return true if the segment uses rails, false otherwise
     */
    public boolean getUseRails() {
        return useRails;
    }

    /**
     * Sets the segment to use rails.
     *
     * @param layTracks the new use rails
     */
    public void setUseRails(boolean layTracks) {
        useRails = layTracks;
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
        this.powerFacing = BlockHelper.rotate90(facing, true);
    }

    /**
     * Gets the power facing direction of the segment.
     *
     * @return the power facing direction
     */
    public BlockFace getPowerFacing() {
        return powerFacing;
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
     * Sets the right side materials of the segment.
     *
     * @param rightMaterials the new right materials
     */
    public void setRightMaterials(ArrayList<Material> rightMaterials) {
        this.rightMaterials = rightMaterials;
    }

    /**
     * Gets the right side materials of the segment.
     *
     * @return the right materials
     */
    public ArrayList<Material> getRightMaterials() {
        return rightMaterials;
    }

    /**
     * Sets the left side materials of the segment.
     *
     * @param leftMaterials the new left materials
     */
    public void setLeftMaterials(ArrayList<Material> leftMaterials) {
        this.leftMaterials = leftMaterials;
    }

    /**
     * Gets the left side materials of the segment.
     *
     * @return the left materials
     */
    public ArrayList<Material> getLeftMaterials() {
        return leftMaterials;
    }

    /**
     * Sets the current section of the segment.
     *
     * @param section the new current section
     */
    public void setCurrentSection(Section section) {
        currentSection = section;
    }

    /**
     * Gets the current section of the segment.
     *
     * @return the current section
     */
    public Section getCurrentSection() {
        return currentSection;
    }

    /**
     * Sets the side index of the segment.
     *
     * @param index the new side index
     */
    public void setSideIndex(int index) {
        sideIndex = index;
    }

    /**
     * Gets the side index of the segment.
     *
     * @return the side index
     */
    public int getSideIndex() {
        return sideIndex;
    }

    /**
     * Sets the negative space of the segment.
     * i.e. the area where no blocks should be placed
     *
     * @param box the new negative space
     */
    public void setNegativeSpace(BoundingBox box) {
        negativeSpace = box;
    }

    /**
     * Gets the negative space of the segment.
     *
     * @return the negative space
     */
    public BoundingBox getNegativeSpace() {
        if (negativeSpace == null) {
            return emptyBox;
        }
        return negativeSpace;
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

    /**
     * Gets if the segment should use powered rails.
     */
    public boolean getUsePower() {
        return usePower;
    }

    /**
     * Sets the segment to use powered rails.
     */
    public void addPower() {
        usePower = true;
        powerLocation = baseLocation.clone();
        powerLocation.add(powerFacing.getModX(), powerFacing.getModY(), powerFacing.getModZ());
    }

    /**
     * Gets the power location of the segment.
     *
     * @return the power location
     */
    public Location getPowerLocation() {
        return powerLocation;
    }
}