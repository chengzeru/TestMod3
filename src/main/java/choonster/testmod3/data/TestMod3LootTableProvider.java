package choonster.testmod3.data;

import choonster.testmod3.TestMod3;
import choonster.testmod3.data.loot.TestMod3BlockLootTables;
import choonster.testmod3.data.loot.TestMod3EntityLootTables;
import choonster.testmod3.data.loot.TestMod3GenericLootTables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Generates this mod's loot tables.
 *
 * @author Choonster
 */
public class TestMod3LootTableProvider extends LootTableProvider {
	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTableGenerators = ImmutableList.of(
			Pair.of(TestMod3BlockLootTables::new, LootParameterSets.BLOCK),
			Pair.of(TestMod3EntityLootTables::new, LootParameterSets.ENTITY),
			Pair.of(TestMod3GenericLootTables::new, LootParameterSets.GENERIC)
	);

	public TestMod3LootTableProvider(final DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
		return lootTableGenerators;
	}

	@Override
	protected void validate(final Map<ResourceLocation, LootTable> map, final ValidationTracker validationtracker) {
		final Set<ResourceLocation> modLootTableIds = LootTables
				.getReadOnlyLootTables()
				.stream()
				.filter(lootTable -> lootTable.getNamespace().equals(TestMod3.MODID))
				.collect(Collectors.toSet());

		for (final ResourceLocation id : Sets.difference(modLootTableIds, map.keySet())) {
			validationtracker.addProblem("Missing mod loot table: " + id);
		}

		map.forEach((id, lootTable) -> {
			LootTableManager.validateLootTable(validationtracker, id, lootTable);
		});
	}

	/**
	 * Gets a name for this provider, to use in logging.
	 */
	@Override
	public String getName() {
		return "TestMod3LootTables";
	}
}
