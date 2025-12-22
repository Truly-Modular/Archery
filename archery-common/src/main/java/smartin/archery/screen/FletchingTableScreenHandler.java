package smartin.archery.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import smartin.archery.Archery;
import smartin.miapi.Miapi;
import smartin.miapi.craft.CraftAction;
import smartin.miapi.material.AllowedMaterial;
import smartin.miapi.material.MaterialProperty;
import smartin.miapi.material.base.Material;
import smartin.miapi.modules.ItemModule;
import smartin.miapi.modules.properties.slot.SlotProperty;
import smartin.miapi.registries.RegistryInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FletchingTableScreenHandler extends AbstractContainerMenu {

    public static final int INPUT_SLOTS = 3;
    public static final int OUTPUT_SLOT = 3;
    public static final int CONTAINER_SIZE = 4;
    private final List<CraftAction> craftActions = new ArrayList<>(3);
    public int count = 0;
    public ItemModule shaftModule = RegistryInventory.ITEM_MODULE_MIAPI_REGISTRY.get(Miapi.id("tm_archery:arrow/shaft/normal"));
    public ItemModule headModule = RegistryInventory.ITEM_MODULE_MIAPI_REGISTRY.get(Miapi.id("tm_archery:arrow/head/normal"));
    public ItemModule tailModule = RegistryInventory.ITEM_MODULE_MIAPI_REGISTRY.get(Miapi.id("tm_archery:arrow/tail/fletching"));


    private final Container container;

    public FletchingTableScreenHandler(
            int containerId,
            Inventory playerInventory
    ) {
        this(Archery.FLETCHING_TABLE_SCREEN_PROVIDER, containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public FletchingTableScreenHandler(
            @Nullable MenuType<?> menuType,
            int containerId,
            Inventory playerInventory,
            Container container
    ) {
        super(menuType, containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        this.container = container;

        // --- Input slots (0–2) ---
        this.addSlot(new Slot(container, 0, 44, 35) {
            public void set(ItemStack stack) {
                super.set(stack);
                slotsChanged(container);
            }
        });
        this.addSlot(new Slot(container, 1, 62, 35) {
            public void set(ItemStack stack) {
                super.set(stack);
                slotsChanged(container);
            }
        });
        this.addSlot(new Slot(container, 2, 80, 35) {
            public void set(ItemStack stack) {
                super.set(stack);
                slotsChanged(container);
            }
        });

        // --- Output slot (3) ---
        this.addSlot(new Slot(container, OUTPUT_SLOT, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                craftAction();
                preview();
            }
        });

        // --- Player inventory ---
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }

        // --- Hotbar ---
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    playerInventory,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        preview();
    }

    /**
     * Stub crafting logic.
     * Replace with recipe lookup or custom logic later.
     */
    protected void preview() {
        ensureActions();
        ItemStack working = new ItemStack(RegistryInventory.modularItem);

        for (int i = 0; i < craftActions.size(); i++) {
            CraftAction action = craftActions.get(i);
            ItemStack input = container.getItem(i);

            if (input.isEmpty()) continue;

            action.setItem(working);
            action.linkInventory(container, i);

            if (!action.canPerform()) {
                container.setItem(OUTPUT_SLOT, ItemStack.EMPTY);
                return;
            }
            //ModuleInstance moduleInstance = action.slotLocation;

            // preview-only execution
            working = action.getPreview();
            if (working.isEmpty()) {
                container.setItem(OUTPUT_SLOT, ItemStack.EMPTY);
                return;
            }
        }

        container.setItem(OUTPUT_SLOT, working);
    }

    public void calculateMaxConsumption() {
        int maxAvailableShaft = calculateMaxAvailable(shaftModule, 0);
        int maxAvailableHead = calculateMaxAvailable(headModule, 1);
        int maxAvailableTail = calculateMaxAvailable(tailModule, 2);

        count = Math.min(1, Math.min(
                maxAvailableHead,
                Math.min(maxAvailableShaft, maxAvailableTail)
        ));
    }

    /**
     * Calculates the maximum available consumption for a given module and container slot.
     * Returns 0 if any required data is missing or invalid.
     */
    private int calculateMaxAvailable(ItemModule module, int slotIndex) {
        if (module == null || container == null) {
            return 0;
        }

        ItemStack stack = container.getItem(slotIndex);
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        // Resolve material
        Material material = MaterialProperty.getMaterialFromIngredient(stack);
        if (material == null) {
            return 0;
        }

        double itemValue = material.getValueOfItem(stack);
        if (itemValue <= 0) {
            return 0;
        }

        // Resolve allowed material property
        Optional<AllowedMaterial.AllowedMaterialData> allowedData =
                AllowedMaterial.property.getData(module);

        if (allowedData.isEmpty()) {
            return 0;
        }

        Float cost = allowedData.get().cost;
        if (cost != null && cost <= 0) {
            return 0;
        }

        return (int) Math.floor(cost / itemValue);
    }

    protected void ensureActions() {
        calculateMaxConsumption();
        if (!craftActions.isEmpty()) return;
        //TODO: add a system to allow for choosing allowed modules
        //TODO: add a system to allow for dynamic amount
        ItemStack modular = new ItemStack(RegistryInventory.modularItem);
        modular.setCount(count);

        // Slot 0 → shaft / base
        craftActions.add(getSimpleCraftAction(List.of(), shaftModule, modular));

        // Slot 1 → head
        craftActions.add(getSimpleCraftAction(List.of("head"), headModule, modular));

        // Slot 2 → modifier (optional)
        craftActions.add(getSimpleCraftAction(List.of("shaft"), tailModule, modular));
    }

    public CraftAction getSimpleCraftAction(List<String> slot, ItemModule module, ItemStack base) {
        CraftAction action = new CraftAction(
                base,
                new SlotProperty.ModuleSlot(),
                module,
                null,
                null,
                new HashMap<>(),
                null
        );
        action.slotLocation.clear();
        action.slotLocation.addAll(slot);
        return action;
    }


    /**
     * Adjust inputs when output is taken.
     */
    protected void craftAction() {
        ensureActions();

        ItemStack working = new ItemStack(RegistryInventory.modularItem);

        if (working.isEmpty()) return;

        for (int i = 0; i < craftActions.size(); i++) {
            CraftAction action = craftActions.get(i);
            ItemStack input = container.getItem(i);

            if (input.isEmpty()) continue;

            action.setItem(working);
            action.linkInventory(container, i);

            if (!action.canPerform()) {
                return;
            }

            working = action.perform();
        }
        container.setChanged();
    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            original = stack.copy();

            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(stack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stack, 0, INPUT_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return original;
    }
}
