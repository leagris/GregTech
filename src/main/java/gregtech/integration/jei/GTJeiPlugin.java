package gregtech.integration.jei;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IWorkable;
import gregtech.api.capability.impl.FuelRecipeMapWorkableHandler;
import gregtech.api.capability.impl.RecipeMapWorkableHandler;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.machines.FuelRecipeMap;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoCategory;
import gregtech.integration.jei.recipe.GTRecipeWrapper;
import gregtech.integration.jei.recipe.RecipeMapCategory;
import gregtech.integration.jei.recipe.fuel.FuelRecipeMapCategory;
import gregtech.integration.jei.recipe.fuel.GTFuelRecipeWrapper;
import gregtech.integration.jei.utils.MetaItemSubtype;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class GTJeiPlugin implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        MetaItemSubtype metaItemSubtype = new MetaItemSubtype();
        for(MetaItem<?> metaItem : MetaItems.ITEMS) {
            subtypeRegistry.registerSubtypeInterpreter(metaItem, metaItemSubtype);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new MultiblockInfoCategory(registry.getJeiHelpers()));
        for(RecipeMap<?> recipeMap : RecipeMap.getRecipeMaps()) {
            registry.addRecipeCategories(new RecipeMapCategory(recipeMap, registry.getJeiHelpers().getGuiHelper()));
        }
        for(FuelRecipeMap fuelRecipeMap : FuelRecipeMap.getRecipeMaps()) {
            registry.addRecipeCategories(new FuelRecipeMapCategory(fuelRecipeMap, registry.getJeiHelpers().getGuiHelper()));
        }
    }

    @Override
    public void register(IModRegistry registry) {
        MultiblockInfoCategory.registerRecipes(registry);
        for(RecipeMap<?> recipeMap : RecipeMap.getRecipeMaps()) {
            List<GTRecipeWrapper> recipesList = recipeMap.getRecipeList()
                .stream().filter(recipe -> !recipe.isHidden() && recipe.hasValidInputsForDisplay())
                .map(r -> new GTRecipeWrapper(recipeMap, r))
                .collect(Collectors.toList());
            registry.addRecipes(recipesList, GTValues.MODID + ":" + recipeMap.unlocalizedName);
        }
        for(FuelRecipeMap fuelRecipeMap : FuelRecipeMap.getRecipeMaps()) {
            List<GTFuelRecipeWrapper> recipeList = fuelRecipeMap.getRecipeList().stream()
                .map(GTFuelRecipeWrapper::new)
                .collect(Collectors.toList());
            registry.addRecipes(recipeList, GTValues.MODID + ":" + fuelRecipeMap.unlocalizedName);
        }

        for(String metaTileEntityId : GregTechAPI.META_TILE_ENTITY_REGISTRY.getKeys()) {
            MetaTileEntity metaTileEntity = GregTechAPI.META_TILE_ENTITY_REGISTRY.getObject(metaTileEntityId);
            if(metaTileEntity.hasCapability(GregtechCapabilities.CAPABILITY_WORKABLE, null)) {
                IWorkable workableCapability = metaTileEntity.getCapability(GregtechCapabilities.CAPABILITY_WORKABLE, null);
                if(workableCapability instanceof RecipeMapWorkableHandler) {
                    RecipeMap<?> recipeMap = ((RecipeMapWorkableHandler) workableCapability).recipeMap;
                    registry.addRecipeCatalyst(metaTileEntity.getStackForm(),
                        GTValues.MODID + ":" + recipeMap.unlocalizedName);
                } else if(workableCapability instanceof FuelRecipeMapWorkableHandler) {
                    FuelRecipeMap recipeMap = ((FuelRecipeMapWorkableHandler) workableCapability).recipeMap;
                    registry.addRecipeCatalyst(metaTileEntity.getStackForm(),
                        GTValues.MODID + ":" + recipeMap.unlocalizedName);
                }
            }
        }
        for(MetaTileEntity breweryTile : MetaTileEntities.BREWERY) {
            registry.addRecipeCatalyst(breweryTile.getStackForm(), VanillaRecipeCategoryUid.BREWING);
        }
        String semiFluidMapId = GTValues.MODID + ":" + RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.getUnlocalizedName();
        registry.addRecipeCatalyst(MetaTileEntities.LARGE_BRONZE_BOILER.getStackForm(), semiFluidMapId);
        registry.addRecipeCatalyst(MetaTileEntities.LARGE_STEEL_BOILER.getStackForm(), semiFluidMapId);
        registry.addRecipeCatalyst(MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm(), semiFluidMapId);
        registry.addRecipeCatalyst(MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER.getStackForm(), semiFluidMapId);
    }
}
