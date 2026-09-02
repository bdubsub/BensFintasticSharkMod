package tfar.bensfintasticsharks.datagen.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.TextComponents;
import tfar.bensfintasticsharks.advancmenets.OctopusInkedTrigger;
import tfar.bensfintasticsharks.advancmenets.PlayerFoundEntityTrigger;
import tfar.bensfintasticsharks.advancmenets.SpyglassSpotSharkTrigger;
import tfar.bensfintasticsharks.entity.CommonThresherSharkEntity;
import tfar.bensfintasticsharks.entity.GreatHammerheadSharkEntity;
import tfar.bensfintasticsharks.entity.GreatWhiteSharkEntity;
import tfar.bensfintasticsharks.init.EntityVariantPredicates;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.function.Consumer;

public class BensFintasticSharksAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {

    private static final EntityType<?>[] MOBS_TO_DISCOVER = new EntityType[]{
            ModEntityTypes.GREAT_WHITE_SHARK, ModEntityTypes.GREAT_HAMMERHEAD_SHARK,
            ModEntityTypes.COMMON_THRESHER_SHARK, ModEntityTypes.SHORTFIN_MAKO_SHARK,
            ModEntityTypes.TIGER_SHARK, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
            ModEntityTypes.SANDTIGER_SHARK, ModEntityTypes.BLACKTIP_REEF_SHARK
    };


    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(ModItems.BFS_LOGO, TextComponents.ROOT, TextComponents.ROOT_DESC,
                        new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"),
                        FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR)
                .addCriterion("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
                .save(saver, BensFintasticSharks.id("root").toString());

        EntityPredicate greatWhiteSharkPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.GREAT_WHITE_SHARK).build();

        Advancement greatWhiteAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.GREAT_WHITE_SHARK_ADVANCEMENT_ICON,TextComponents.GREAT_WHITE_ENCOUNTER,TextComponents.GREAT_WHITE_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(greatWhiteSharkPredicate))
                .save(saver, BensFintasticSharks.id("great_white_encounter").toString());

        EntityPredicate greatHammerheadSharkPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.GREAT_HAMMERHEAD_SHARK).build();

        Advancement greatHammerHeadAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.GREAT_HAMMERHEAD_SHARK_ADVANCEMENT_ICON,TextComponents.GREAT_HAMMERHEAD_ENCOUNTER,TextComponents.GREAT_HAMMERHEAD_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(greatHammerheadSharkPredicate))
                .save(saver, BensFintasticSharks.id("great_hammerhead_encounter").toString());

        EntityPredicate commonThresherPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.COMMON_THRESHER_SHARK).build();

        Advancement commonThresherAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.COMMON_THRESHER_SHARK_ADVANCEMENT_ICON,TextComponents.COMMON_THRESHER_ENCOUNTER,TextComponents.COMMON_THRESHER_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(commonThresherPredicate))
                .save(saver, BensFintasticSharks.id("common_thresher_encounter").toString());

        EntityPredicate shortfinMakoPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.SHORTFIN_MAKO_SHARK).build();

        Advancement shortfinMakoAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.SHORTFIN_MAKO_SHARK_ADVANCEMENT_ICON,TextComponents.SHORTFIN_MAKO_ENCOUNTER,TextComponents.SHORTFIN_MAKO_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(shortfinMakoPredicate))
                .save(saver, BensFintasticSharks.id("shortfin_mako_encounter").toString());

        Advancement killedByShark = Advancement.Builder.advancement().parent(root)
                .display(ModItems.SLEEPING_WITH_THE_FISHES,TextComponents.SLEEPING_WITH_THE_FISHES, TextComponents.SLEEPING_WITH_THE_FISHES_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("killed_by_shark", KilledTrigger.TriggerInstance.entityKilledPlayer(EntityPredicate.Builder.entity().of(ModTags.EntityTypes.SHARKS)))
                .save(saver, BensFintasticSharks.id("sleeping_with_the_fishes").toString());


        EntityPredicate harborSealPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.HARBOR_SEAL).build();

        Advancement harborSealAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.HARBOR_SEAL_BLOCK,TextComponents.HARBOR_SEAL_ENCOUNTER,TextComponents.HARBOR_SEAL_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(harborSealPredicate))
                .save(saver, BensFintasticSharks.id("harbor_seal_encounter").toString());

        EntityPredicate albinoGreatWhitePredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.GREAT_WHITE_SHARK)
                .subPredicate(EntityVariantPredicates.GREAT_WHITE_SHARK.createPredicate(GreatWhiteSharkEntity.Variant.ALBINO)).build();

        EntityPredicate albinoGreatHammerheadPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.GREAT_HAMMERHEAD_SHARK)
                .subPredicate(EntityVariantPredicates.GREAT_HAMMERHEAD_SHARK.createPredicate(GreatHammerheadSharkEntity.Variant.ALBINO)).build();

        EntityPredicate albinoCommonThresherPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.COMMON_THRESHER_SHARK)
                .subPredicate(EntityVariantPredicates.COMMON_THRESHER_SHARK.createPredicate(CommonThresherSharkEntity.Variant.ALBINO)).build();

        Advancement albinoAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.ALBINO,TextComponents.ALBINO_ENCOUNTER,TextComponents.ALBINO_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR)
                .addCriterion("player_found_albino_great_white", PlayerFoundEntityTrigger.TriggerInstance.located(albinoGreatWhitePredicate))
                .addCriterion("player_found_albino_hammerhead", PlayerFoundEntityTrigger.TriggerInstance.located(albinoGreatHammerheadPredicate))
                .addCriterion("player_found_albino_thresher", PlayerFoundEntityTrigger.TriggerInstance.located(albinoCommonThresherPredicate))
                .save(saver, BensFintasticSharks.id("albino_encounter").toString());

        Advancement illegalPoaching = Advancement.Builder.advancement().parent(root)
                .display(ModItems.ILLEGAL_POACHING, TextComponents.ILLEGAL_POACHING, TextComponents.ILLEGAL_POACHING_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_killed_entity",
                        KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModTags.EntityTypes.SHARKS)))
                .save(saver, BensFintasticSharks.id("illegal_poaching").toString());

        // "Crikey! Respect the wildlife!" — get stung by a stingray (was a kill trigger;
        // repurposed per Ben's 0.16 feedback, replacing the old "Crankey!" advancement).
        Advancement justiceForSteve = Advancement.Builder.advancement().parent(root)
                .display(ModItems.JUSTICE_FOR_STEVE, TextComponents.JUSTICE_FOR_STEVE, TextComponents.JUSTICE_FOR_STEVE_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("entity_hurt_player",
                        EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(
                                DamagePredicate.Builder.damageInstance()
                                        .type(DamageSourcePredicate.Builder.damageType()
                                                .direct(EntityPredicate.Builder.entity().of(ModEntityTypes.COMMON_STINGRAY)))))
                .save(saver, BensFintasticSharks.id("justice_for_steve").toString());

        Advancement.Builder.advancement().parent(root)
                .display(ModItems.PRISMARINE_CHESTPLATE,TextComponents.PRISMARINE_ARMOR,TextComponents.PRISMARINE_ARMOR_DESC, null, FrameType.CHALLENGE, true, true, false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("prismarine_armor", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.PRISMARINE_HELMET, ModItems.PRISMARINE_CHESTPLATE, ModItems.PRISMARINE_LEGGINGS,ModItems.PRISMARINE_BOOTS))
                .save(saver, BensFintasticSharks.id("prismarine_armor").toString());


        EntityPredicate zippyPredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.COMMON_THRESHER_SHARK)
                .subPredicate(EntityVariantPredicates.COMMON_THRESHER_SHARK.createPredicate(CommonThresherSharkEntity.Variant.ZIPPY)).build();

        Advancement zippyAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.ZIPPY_PIXEL_ART,TextComponents.ZIPPY_ENCOUNTER,TextComponents.ZIPPY_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(zippyPredicate))
                .save(saver, BensFintasticSharks.id("zippy_encounter").toString());

        EntityPredicate specimen8Predicate = EntityPredicate.Builder.entity().of(ModEntityTypes.GREAT_WHITE_SHARK)
                .subPredicate(EntityVariantPredicates.GREAT_WHITE_SHARK.createPredicate(GreatWhiteSharkEntity.Variant.SPECIMEN_8)).build();

        Advancement specimen8Advancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.SPECIMEN_8,TextComponents.SPECIMEN_8_ENCOUNTER,TextComponents.SPECIMEN_8_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(specimen8Predicate))
                .save(saver, BensFintasticSharks.id("specimen_8_encounter").toString());

        EntityPredicate deepBluePredicate = EntityPredicate.Builder.entity().of(ModEntityTypes.GREAT_WHITE_SHARK)
                .subPredicate(EntityVariantPredicates.GREAT_WHITE_SHARK.createPredicate(GreatWhiteSharkEntity.Variant.DEEP_BLUE)).build();

        Advancement deepBlueAdvancement = Advancement.Builder.advancement().parent(root)
                .display(ModItems.MOMMY_SHARK,TextComponents.DEEP_BLUE_ENCOUNTER,TextComponents.DEEP_BLUE_ENCOUNTER_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("player_found_entity", PlayerFoundEntityTrigger.TriggerInstance.located(deepBluePredicate))
                .save(saver, BensFintasticSharks.id("deep_blue_encounter").toString());

        Advancement sharkCodex = Advancement.Builder.advancement().parent(root)
                .display(ModItems.SHARK_CODEX,TextComponents.SHARK_CODEX, TextComponents.SHARK_CODEX_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("shark_codex", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SHARK_CODEX))
                .save(saver, BensFintasticSharks.id("shark_codex").toString());

        Advancement lostManuscript = Advancement.Builder.advancement().parent(root)
                .display(ModItems.LOST_MANUSCRIPT,TextComponents.LOST_MANUSCRIPT, TextComponents.LOST_MANUSCRIPT_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("lost_manuscript", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.LOST_MANUSCRIPT))
                .save(saver, BensFintasticSharks.id("lost_manuscript").toString());

        Advancement levelSharkCodex = Advancement.Builder.advancement().parent(sharkCodex)
                .display(ModItems.CODEX_VOLUME,TextComponents.LEVEL_SHARK_CODEX, TextComponents.LEVEL_SHARK_CODEX_DESC, null, FrameType.TASK, true, true, false)
                .addCriterion("level_shark_codex", RecipeCraftedTrigger.TriggerInstance.craftedItem(BensFintasticSharks.id("codex_volume")))
                .save(saver, BensFintasticSharks.id("level_shark_codex").toString());

        Advancement sharksGalore = addMobsToDiscover(Advancement.Builder.advancement().parent(sharkCodex)
                .display(ModItems.SHARKS_GALORE,TextComponents.SHARKS_GALORE, TextComponents.SHARKS_GALORE_DESC, null, FrameType.CHALLENGE, true, true, false))
                .save(saver, BensFintasticSharks.id("sharks_galore").toString());

        // ---- Legacy 1.0 advancements ----

        Advancement.Builder marineCuriousBuilder = Advancement.Builder.advancement().parent(root)
                .display(ModItems.TIGER_SHARK_SPAWN_EGG,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.marine_curious.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.marine_curious.description"),
                        null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR);
        for (EntityType<?> type : ALL_BFS_SPECIES) {
            marineCuriousBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath(),
                    PlayerFoundEntityTrigger.TriggerInstance.located(
                            EntityPredicate.Builder.entity().of(type).build()));
        }
        Advancement marineCurious = marineCuriousBuilder.save(
                saver, BensFintasticSharks.id("marine_curious").toString());

        Advancement sharkSpotter = Advancement.Builder.advancement().parent(marineCurious)
                .display(Items.SPYGLASS,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.shark_spotter.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.shark_spotter.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("spotted_shark", SpyglassSpotSharkTrigger.TriggerInstance.spotted())
                .save(saver, BensFintasticSharks.id("shark_spotter").toString());

        // Shark Whisperer — encounter all 8 shark species
        Advancement.Builder sharkWhispererBuilder = Advancement.Builder.advancement().parent(sharkSpotter)
                .display(ModItems.SHARKS_GALORE,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.shark_whisperer.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.shark_whisperer.description"),
                        null, FrameType.CHALLENGE, true, true, false)
                .rewards(AdvancementRewards.Builder.experience(100));
        for (EntityType<?> shark : ALL_SHARKS) {
            sharkWhispererBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(shark).getPath(),
                    PlayerFoundEntityTrigger.TriggerInstance.located(
                            EntityPredicate.Builder.entity().of(shark).build()));
        }
        Advancement sharkWhisperer = sharkWhispererBuilder.save(saver, BensFintasticSharks.id("shark_whisperer").toString());

        // Apex Awareness — be attacked by a shark (and survive it; no kill condition required here)
        Advancement.Builder apexAwarenessBuilder = Advancement.Builder.advancement().parent(marineCurious)
                .display(ModItems.GREAT_WHITE_SHARK_TOOTH,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.apex_awareness.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.apex_awareness.description"),
                        null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR);
        for (EntityType<?> shark : ALL_SHARKS) {
            apexAwarenessBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(shark).getPath(),
                    EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(
                            DamagePredicate.Builder.damageInstance()
                                    .type(DamageSourcePredicate.Builder.damageType()
                                            .direct(EntityPredicate.Builder.entity().of(shark)))));
        }
        apexAwarenessBuilder.save(saver, BensFintasticSharks.id("apex_awareness").toString());

        // Conservationist — same triggers as Shark Spotter, framed as the gentle path.
        // Icon: non-bloody Shark Jaws sprite (Ben's "Shark Skeleton Jaws").
        Advancement.Builder conservationistBuilder = Advancement.Builder.advancement().parent(sharkSpotter)
                .display(ModItems.SHARK_JAWS,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.conservationist.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.conservationist.description"),
                        null, FrameType.GOAL, true, true, false)
                .requirements(RequirementsStrategy.AND);
        // Three explicit shark encounters
        for (int i = 0; i < 3 && i < ALL_SHARKS.length; i++) {
            EntityType<?> shark = ALL_SHARKS[i];
            conservationistBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(shark).getPath(),
                    PlayerFoundEntityTrigger.TriggerInstance.located(
                            EntityPredicate.Builder.entity().of(shark).build()));
        }
        conservationistBuilder.save(saver, BensFintasticSharks.id("conservationist").toString());

        // Marine Biologist — encounter every BFS species
        Advancement.Builder marineBiologistBuilder = Advancement.Builder.advancement().parent(sharkWhisperer)
                .display(ModItems.SHARK_CODEX,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.marine_biologist.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.marine_biologist.description"),
                        null, FrameType.CHALLENGE, true, true, false)
                .rewards(AdvancementRewards.Builder.experience(250))
                .requirements(RequirementsStrategy.AND);
        for (EntityType<?> mob : ALL_BFS_SPECIES) {
            marineBiologistBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(mob).getPath(),
                    PlayerFoundEntityTrigger.TriggerInstance.located(
                            EntityPredicate.Builder.entity().of(mob).build()));
        }
        marineBiologistBuilder.save(saver, BensFintasticSharks.id("marine_biologist").toString());

        // Wrong Place, Wrong Time — be attacked by a shark
        Advancement.Builder wrongPlaceBuilder = Advancement.Builder.advancement().parent(marineCurious)
                .display(ModItems.GREAT_HAMMERHEAD_SHARK_TOOTH,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.wrong_place_wrong_time.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.wrong_place_wrong_time.description"),
                        null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR);
        for (EntityType<?> shark : ALL_SHARKS) {
            wrongPlaceBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(shark).getPath(),
                    EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(
                            DamagePredicate.Builder.damageInstance()
                                    .type(DamageSourcePredicate.Builder.damageType()
                                            .direct(EntityPredicate.Builder.entity().of(shark)))));
        }
        wrongPlaceBuilder.save(saver, BensFintasticSharks.id("wrong_place_wrong_time").toString());

        // Stung! — damaged by a jellyfish
        Advancement.Builder stungBuilder = Advancement.Builder.advancement().parent(marineCurious)
                .display(ModItems.BLACK_SEA_NETTLE_JELLYFISH_SPAWN_EGG,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.stung.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.stung.description"),
                        null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR);
        stungBuilder.addCriterion("black_sea_nettle", EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(
                DamagePredicate.Builder.damageInstance()
                        .type(DamageSourcePredicate.Builder.damageType()
                                .direct(EntityPredicate.Builder.entity().of(ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH)))));
        stungBuilder.addCriterion("cannonball", EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(
                DamagePredicate.Builder.damageInstance()
                        .type(DamageSourcePredicate.Builder.damageType()
                                .direct(EntityPredicate.Builder.entity().of(ModEntityTypes.CANNONBALL_JELLYFISH)))));
        stungBuilder.save(saver, BensFintasticSharks.id("stung").toString());

        // Inked — provoke an octopus into inking by hurting it. The old version used
        // PlayerFoundEntityTrigger as a placeholder and popped on a mere encounter; the
        // custom trigger fires from the octopus hurt() path for the attacking player only.
        Advancement.Builder inkedBuilder = Advancement.Builder.advancement().parent(marineCurious)
                .display(ModItems.COMMON_OCTOPUS_SPAWN_EGG,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.inked.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.inked.description"),
                        null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR);
        inkedBuilder.addCriterion("common_octopus", OctopusInkedTrigger.TriggerInstance.inked(
                EntityPredicate.Builder.entity().of(ModEntityTypes.COMMON_OCTOPUS).build()));
        inkedBuilder.addCriterion("caribbean_reef_octopus", OctopusInkedTrigger.TriggerInstance.inked(
                EntityPredicate.Builder.entity().of(ModEntityTypes.CARIBBEAN_REEF_OCTOPUS).build()));
        inkedBuilder.save(saver, BensFintasticSharks.id("inked").toString());

        // Dolphin Friend — encounter a dolphin
        Advancement.Builder dolphinFriendBuilder = Advancement.Builder.advancement().parent(marineCurious)
                .display(ModItems.BOTTLENOSE_DOLPHIN_SPAWN_EGG,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.dolphin_friend.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.dolphin_friend.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("found_dolphin", PlayerFoundEntityTrigger.TriggerInstance.located(
                        EntityPredicate.Builder.entity().of(ModEntityTypes.BOTTLENOSE_DOLPHIN).build()));
        dolphinFriendBuilder.save(saver, BensFintasticSharks.id("dolphin_friend").toString());

        // Apex of Apex — encounter an Orca
        Advancement apexOfApex = Advancement.Builder.advancement().parent(sharkWhisperer)
                .display(ModItems.ORCA_SPAWN_EGG,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.apex_of_apex.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.apex_of_apex.description"),
                        null, FrameType.GOAL, true, true, false)
                .addCriterion("found_orca", PlayerFoundEntityTrigger.TriggerInstance.located(
                        EntityPredicate.Builder.entity().of(ModEntityTypes.ORCA).build()))
                .save(saver, BensFintasticSharks.id("apex_of_apex").toString());

        // Fresh Catch — cook a lobster (have cooked claw or tail in inventory)
        Advancement freshCatch = Advancement.Builder.advancement().parent(marineCurious)
                .display(ModItems.COOKED_LOBSTER_CLAW,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.fresh_catch.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.fresh_catch.description"),
                        null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.OR)
                .addCriterion("cooked_lobster_claw", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COOKED_LOBSTER_CLAW))
                .addCriterion("cooked_lobster_tail", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COOKED_LOBSTER_TAIL))
                .save(saver, BensFintasticSharks.id("fresh_catch").toString());

        // Hidden Trove — have lost manuscript (proxy for finding the trove)
        Advancement hiddenTrove = Advancement.Builder.advancement().parent(root)
                .display(Items.CHEST,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.hidden_trove.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.hidden_trove.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("found_manuscript", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.LOST_MANUSCRIPT))
                .save(saver, BensFintasticSharks.id("hidden_trove").toString());

        // Captain's Heir — obtain Captain Ben's Hat
        Advancement captainsHeir = Advancement.Builder.advancement().parent(hiddenTrove)
                .display(ModItems.CAPTAIN_BEN_HAT,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.captains_heir.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.captains_heir.description"),
                        null, FrameType.GOAL, true, true, true)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("got_hat", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CAPTAIN_BEN_HAT))
                .save(saver, BensFintasticSharks.id("captains_heir").toString());

        // Fancy Fork — obtain a Shark Trident.
        Advancement.Builder.advancement().parent(root)
                .display(ModItems.SHARK_TRIDENT,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.fancy_fork.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.fancy_fork.description"),
                        null, FrameType.GOAL, true, true, false)
                .addCriterion("got_shark_trident", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SHARK_TRIDENT))
                .save(saver, BensFintasticSharks.id("fancy_fork").toString());

        // 0.18 — "Source: trust me bro" — buy Ben's totally-legit carved-netherite Megalodon
        // Tooth from a master fisherman. Challenge frame: it costs a fortune.
        Advancement.Builder.advancement().parent(root)
                .display(ModItems.MEGALODON_TOOTH,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.source_trust_me_bro.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.source_trust_me_bro.description"),
                        null, FrameType.CHALLENGE, true, true, false)
                .addCriterion("got_megalodon_tooth", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MEGALODON_TOOTH))
                .save(saver, BensFintasticSharks.id("source_trust_me_bro").toString());

        // Per-species encounter advancements for the Legacy 1.0 species. Each one is a
        // simple "you were within range of this entity" tick-time trigger, displayed as
        // its spawn egg under marine_curious. Names match the translation keys in
        // ModLangProvider.
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.TIGER_SHARK,
                ModItems.TIGER_SHARK_ADVANCEMENT_ICON, "tiger_shark_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                ModItems.OCEANIC_WHITETIP_SHARK_ADVANCEMENT_ICON, "oceanic_whitetip_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.SANDTIGER_SHARK,
                ModItems.SANDTIGER_SHARK_ADVANCEMENT_ICON, "sandtiger_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.BLACKTIP_REEF_SHARK,
                ModItems.BLACKTIP_REEF_SHARK_ADVANCEMENT_ICON, "blacktip_reef_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.COMMON_OCTOPUS,
                ModItems.COMMON_OCTOPUS_SPAWN_EGG, "common_octopus_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.CARIBBEAN_REEF_OCTOPUS,
                ModItems.CARIBBEAN_REEF_OCTOPUS_SPAWN_EGG, "caribbean_reef_octopus_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.NAUTILUS,
                ModItems.NAUTILUS_SPAWN_EGG, "nautilus_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.GIANT_MORAY_EEL,
                ModItems.GIANT_MORAY_EEL_SPAWN_EGG, "giant_moray_eel_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.GREEN_SEA_TURTLE,
                ModItems.GREEN_SEA_TURTLE_SPAWN_EGG, "green_sea_turtle_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.AMERICAN_LOBSTER,
                ModItems.AMERICAN_LOBSTER_SPAWN_EGG, "american_lobster_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH,
                ModItems.BLACK_SEA_NETTLE_JELLYFISH_SPAWN_EGG, "black_sea_nettle_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.CANNONBALL_JELLYFISH,
                ModItems.CANNONBALL_JELLYFISH_SPAWN_EGG, "cannonball_jellyfish_encounter");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.ATLANTIC_COD,
                ModItems.RAW_ATLANTIC_COD, "gadus_morhua");
        addEncounterAdvancement(saver, marineCurious, ModEntityTypes.ATLANTIC_SALMON,
                ModItems.RAW_ATLANTIC_SALMON, "salmo_salar");

        Advancement.Builder.advancement().parent(root)
                .display(ModItems.COOKED_ATLANTIC_COD,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.oh_my_cod.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.oh_my_cod.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("caught_atlantic_cod", FishingRodHookedTrigger.TriggerInstance.fishedItem(
                        ItemPredicate.Builder.item().build(),
                        EntityPredicate.Builder.entity().build(),
                        ItemPredicate.Builder.item().of(ModItems.RAW_ATLANTIC_COD).build()))
                .save(saver, BensFintasticSharks.id("oh_my_cod").toString());

        Advancement.Builder.advancement().parent(root)
                .display(ModItems.COOKED_ATLANTIC_SALMON,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.why_arent_you_red.title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks.why_arent_you_red.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("caught_atlantic_salmon", FishingRodHookedTrigger.TriggerInstance.fishedItem(
                        ItemPredicate.Builder.item().build(),
                        EntityPredicate.Builder.entity().build(),
                        ItemPredicate.Builder.item().of(ModItems.RAW_ATLANTIC_SALMON).build()))
                .save(saver, BensFintasticSharks.id("why_arent_you_red").toString());
        // Bottlenose Dolphin already has its own advancement: dolphin_friend.
    }

    private static void addEncounterAdvancement(java.util.function.Consumer<Advancement> saver,
                                                Advancement parent,
                                                EntityType<?> type,
                                                net.minecraft.world.item.Item icon,
                                                String id) {
        Advancement.Builder.advancement().parent(parent)
                .display(icon,
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks." + id + ".title"),
                        net.minecraft.network.chat.Component.translatable("advancements.bensfintasticsharks." + id + ".description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("found", PlayerFoundEntityTrigger.TriggerInstance.located(
                        EntityPredicate.Builder.entity().of(type).build()))
                .save(saver, BensFintasticSharks.id(id).toString());
    }

    private static final EntityType<?>[] ALL_SHARKS = new EntityType[] {
            ModEntityTypes.GREAT_WHITE_SHARK, ModEntityTypes.GREAT_HAMMERHEAD_SHARK,
            ModEntityTypes.COMMON_THRESHER_SHARK, ModEntityTypes.SHORTFIN_MAKO_SHARK,
            ModEntityTypes.TIGER_SHARK, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
            ModEntityTypes.SANDTIGER_SHARK, ModEntityTypes.BLACKTIP_REEF_SHARK
    };

    private static final EntityType<?>[] ALL_BFS_SPECIES = new EntityType[] {
            ModEntityTypes.GREAT_WHITE_SHARK, ModEntityTypes.GREAT_HAMMERHEAD_SHARK,
            ModEntityTypes.COMMON_THRESHER_SHARK, ModEntityTypes.SHORTFIN_MAKO_SHARK,
            ModEntityTypes.TIGER_SHARK, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
            ModEntityTypes.SANDTIGER_SHARK, ModEntityTypes.BLACKTIP_REEF_SHARK,
            ModEntityTypes.ORCA, ModEntityTypes.BOTTLENOSE_DOLPHIN,
            ModEntityTypes.COMMON_OCTOPUS, ModEntityTypes.CARIBBEAN_REEF_OCTOPUS,
            ModEntityTypes.NAUTILUS, ModEntityTypes.GIANT_MORAY_EEL,
            ModEntityTypes.GREEN_SEA_TURTLE, ModEntityTypes.AMERICAN_LOBSTER,
            ModEntityTypes.HARBOR_SEAL, ModEntityTypes.COMMON_STINGRAY,
            ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH, ModEntityTypes.CANNONBALL_JELLYFISH,
            ModEntityTypes.ATLANTIC_COD, ModEntityTypes.ATLANTIC_SALMON
    };

    private static Advancement.Builder addMobsToDiscover(Advancement.Builder pBuilder) {
        for(EntityType<?> entitytype : MOBS_TO_DISCOVER) {
            pBuilder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(entitytype).toString(), PlayerFoundEntityTrigger.TriggerInstance.located(EntityPredicate.Builder.entity().of(entitytype).build()));
        }

        return pBuilder;
    }

}
//b. “You’re gonna need a bigger boat…” (Encounter a Great White)
//c. “Stop! Hammer Time!” (Encounter a Great Hammerhead)
//d. “Whiplash!” (Encounter a Common Thresher)
//e. “Sleeping with the fishes.” (Killed by a shark)
//f. “Awkward.” (Encounter a Harbor Seal)
//g. “Crankey!” (Get stung by a Common Stingray)
//h. “It's a shiny!” (Encounter an albino variant)
//i. “Illegal Poaching” (Kill a shark)
//j. “Unethical.” (Kill a Harbor Seal)
//k. “Justice for Steve” (Kill a Common Stingray)
//l. “The Sea Dwelling Knight” (Obtain a full set of Prismarine Armor)
//m. “Shark of Zeus” (Discover Zippy)
//n. “I’ll be back.” (Discover Specimen-8)
//o. “Mommy Shark” (Discover Deep Blue)
//17
//p. “Knowledge is power…” (Craft a Shark Codex)
//q. “Lost beneath the waves” (Find a lost manuscript)
//r. “Level Up!” (Combine 9 shark Codex Pages with a Shark Codex)
//s. “Shark Galore!” (Discover every species of sharks)
