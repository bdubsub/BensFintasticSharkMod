package tfar.bensfintasticsharks.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.server.level.ServerLevel;
import tfar.bensfintasticsharks.diagnostics.AlgaeDiagnostics;

import java.util.LinkedHashMap;
import java.util.Map;

/** Blocks added by the mod. */
public final class ModBlocks {

    public static final Block ALGAE_BLOCK = new SmallAlgaeBlock();
    public static final LargeAlgaeBlock LARGE_GREEN_ALGAE = new LargeAlgaeBlock();
    public static final LargeAlgaeBlock LARGE_RED_ALGAE = new LargeAlgaeBlock();

    private ModBlocks() {
    }

    /** Small algae uses the vanilla multiface and waterlogged implementation. */
    private static final class SmallAlgaeBlock extends GlowLichenBlock {
        private SmallAlgaeBlock() {
            super(BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).noOcclusion());
            registerDefaultState(defaultBlockState()
                    .setValue(BlockStateProperties.DOWN, true)
                    .setValue(BlockStateProperties.WATERLOGGED, true));
        }

        @Override
        protected boolean isFaceSupported(Direction direction) {
            return direction != Direction.UP;
        }

        @Override
        public boolean isValidBonemealTarget(net.minecraft.world.level.LevelReader level,
                                             BlockPos pos,
                                             BlockState state,
                                             boolean isClient) {
            return false;
        }

        @Override
        public boolean isBonemealSuccess(Level level,
                                         RandomSource random,
                                         BlockPos pos,
                                         BlockState state) {
            return false;
        }

        @Override
        public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour,
                                      LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
            BlockState updated = super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
            if (direction != Direction.UP && !updated.equals(state)) {
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("supportDirection", direction.getName());
                details.put("facesBefore", faces(state));
                details.put("facesAfter", updated.is(this) ? faces(updated) : "removed");
                details.put("waterBefore", state.getValue(BlockStateProperties.WATERLOGGED));
                details.put("waterAfter", updated.is(this)
                        ? updated.getValue(BlockStateProperties.WATERLOGGED) : "removed");
                AlgaeDiagnostics.emit(level, pos, "algae_support",
                        !updated.is(this) || faces(updated).isEmpty() ? "support_removed" : "support_reconciled",
                        null, state, updated, details);
            }
            return updated;
        }

        private static String faces(BlockState state) {
            StringBuilder faces = new StringBuilder();
            appendFace(faces, state, "north", BlockStateProperties.NORTH);
            appendFace(faces, state, "south", BlockStateProperties.SOUTH);
            appendFace(faces, state, "east", BlockStateProperties.EAST);
            appendFace(faces, state, "west", BlockStateProperties.WEST);
            appendFace(faces, state, "down", BlockStateProperties.DOWN);
            return faces.toString();
        }

        private static void appendFace(StringBuilder faces, BlockState state, String name,
                                       net.minecraft.world.level.block.state.properties.BooleanProperty property) {
            if (state.getValue(property)) {
                if (!faces.isEmpty()) faces.append(',');
                faces.append(name);
            }
        }
    }

    /** One public block identity represents every segment of a large algae column. */
    public static final class LargeAlgaeBlock extends Block implements BonemealableBlock {
        public static final EnumProperty<AlgaeSegment> SEGMENT =
                EnumProperty.create("segment", AlgaeSegment.class);
        public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
        public static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED =
                BlockStateProperties.WATERLOGGED;
        private static final int MAX_HEIGHT = 8;

        private LargeAlgaeBlock() {
            super(BlockBehaviour.Properties.copy(Blocks.KELP).noOcclusion());
            registerDefaultState(stateDefinition.any()
                    .setValue(SEGMENT, AlgaeSegment.SINGLE)
                    .setValue(AGE, 0)
                    .setValue(WATERLOGGED, true));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(SEGMENT, AGE, WATERLOGGED);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            BlockPos pos = context.getClickedPos();
            FluidState fluid = context.getLevel().getFluidState(pos);
            if (!fluid.is(FluidTags.WATER) || !fluid.isSource()) {
                return null;
            }
            BlockState below = context.getLevel().getBlockState(pos.below());
            if (below.is(this) && columnHeight(context.getLevel(), pos.below()) >= MAX_HEIGHT) {
                return null;
            }
            AlgaeSegment segment = below.is(this) ? AlgaeSegment.TOP : AlgaeSegment.SINGLE;
            return defaultBlockState().setValue(SEGMENT, segment);
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            BlockState below = level.getBlockState(pos.below());
            if (state.getValue(SEGMENT) != AlgaeSegment.SINGLE) {
                return below.is(this);
            }
            return !below.is(this) && Block.canSupportCenter(level, pos.below(), Direction.UP);
        }

        @Override
        public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
            return false;
        }

        @Override
        public BlockState updateShape(BlockState state,
                                      Direction direction,
                                      BlockState neighbour,
                                      LevelAccessor level,
                                      BlockPos pos,
                                      BlockPos neighbourPos) {
            BlockState updated = state;
            if (direction == Direction.DOWN) {
                boolean supported = neighbour.is(this)
                        || Block.canSupportCenter(level, neighbourPos, Direction.UP);
                if (!supported) {
                    updated = waterState(state);
                    emitSupport(level, pos, state, updated, direction, "support_removed");
                    return updated;
                }
                if (neighbour.is(this) && state.getValue(SEGMENT) == AlgaeSegment.SINGLE) {
                    updated = state.setValue(SEGMENT, AlgaeSegment.TOP);
                }
            }
            if (direction == Direction.UP) {
                if (neighbour.is(this)) {
                    if (state.getValue(SEGMENT) != AlgaeSegment.BODY) {
                        updated = state.setValue(SEGMENT, AlgaeSegment.BODY);
                    }
                } else if (state.getValue(SEGMENT) == AlgaeSegment.BODY) {
                    updated = state.setValue(SEGMENT, AlgaeSegment.TOP);
                }
            }
            if (!updated.equals(state)) {
                emitSupport(level, pos, state, updated, direction, "support_reconciled");
            }
            return updated;
        }

        @Override
        public VoxelShape getShape(BlockState state,
                                   BlockGetter level,
                                   BlockPos pos,
                                   CollisionContext context) {
            return Shapes.empty();
        }

        @Override
        public FluidState getFluidState(BlockState state) {
            return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return state.getValue(SEGMENT) != AlgaeSegment.BODY && state.getValue(AGE) < 25;
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            if (random.nextInt(7) == 0) {
                grow(level, pos, state);
            }
        }

        @Override
        public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
            return state.getValue(SEGMENT) != AlgaeSegment.BODY
                    && columnHeight(level, pos) < MAX_HEIGHT
                    && canGrowInto(level, pos.above());
        }

        @Override
        public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
            return isValidBonemealTarget(level, pos, state, level.isClientSide);
        }

        @Override
        public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
            grow(level, pos, state);
        }

        @Override
        public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
            super.onPlace(state, level, pos, oldState, movedByPiston);
            if (!level.isClientSide && oldState.is(this) && !oldState.equals(state)) {
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("source", "state_normalization");
                details.put("legacyState", false);
                details.put("segmentBefore", oldState.getValue(SEGMENT).getSerializedName());
                details.put("segmentAfter", state.getValue(SEGMENT).getSerializedName());
                details.put("waterPreserved", state.getValue(WATERLOGGED));
                AlgaeDiagnostics.emit(level, pos, "algae_migrate", "state_normalized", null,
                        oldState, state, details);
            }
        }

        private boolean canGrowInto(LevelReader level, BlockPos pos) {
            FluidState fluid = level.getFluidState(pos);
            return fluid.is(FluidTags.WATER) && fluid.isSource() && level.getBlockState(pos).is(Blocks.WATER);
        }

        private void grow(LevelAccessor level, BlockPos pos, BlockState state) {
            if (!canGrowInto(level, pos.above()) || columnHeight(level, pos) >= MAX_HEIGHT) {
                return;
            }
            int heightBefore = columnHeight(level, pos);
            int nextAge = Math.min(25, state.getValue(AGE) + 1);
            level.setBlock(pos, state.setValue(SEGMENT, AlgaeSegment.BODY), UPDATE_CLIENTS | UPDATE_NEIGHBORS);
            level.setBlock(pos.above(), defaultBlockState()
                    .setValue(SEGMENT, AlgaeSegment.TOP)
                    .setValue(AGE, nextAge), UPDATE_CLIENTS | UPDATE_NEIGHBORS);
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("growthSource", "random_or_bonemeal");
            details.put("heightBefore", heightBefore);
            details.put("heightAfter", columnHeight(level, pos));
            details.put("ageBefore", state.getValue(AGE));
            details.put("ageAfter", nextAge);
            details.put("sourceWater", true);
            AlgaeDiagnostics.emit(level, pos, "algae_grow", "accepted", null, state,
                    level.getBlockState(pos), details);
        }

        public void placeNaturalColumn(LevelAccessor level, BlockPos base, int height) {
            int boundedHeight = Math.max(1, Math.min(MAX_HEIGHT, height));
            for (int index = 0; index < boundedHeight; index++) {
                AlgaeSegment segment = boundedHeight == 1
                        ? AlgaeSegment.SINGLE
                        : index == boundedHeight - 1 ? AlgaeSegment.TOP
                        : index == 0 ? AlgaeSegment.SINGLE : AlgaeSegment.BODY;
                level.setBlock(base.above(index), defaultBlockState()
                        .setValue(SEGMENT, segment)
                        .setValue(AGE, 0)
                        .setValue(WATERLOGGED, true), UPDATE_CLIENTS | UPDATE_NEIGHBORS);
            }
        }

        private int columnHeight(LevelReader level, BlockPos pos) {
            int height = 1;
            BlockPos cursor = pos.below();
            while (height < MAX_HEIGHT && level.getBlockState(cursor).is(this)) {
                height++;
                cursor = cursor.below();
            }
            cursor = pos.above();
            while (height < MAX_HEIGHT && level.getBlockState(cursor).is(this)) {
                height++;
                cursor = cursor.above();
            }
            return height;
        }

        private BlockState waterState(BlockState state) {
            return Fluids.WATER.getSource(false).createLegacyBlock();
        }

        private static void emitSupport(LevelAccessor level, BlockPos pos, BlockState before,
                                        BlockState after, Direction direction, String reason) {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("supportDirection", direction.getName());
            details.put("segmentBefore", before.getValue(SEGMENT).getSerializedName());
            details.put("segmentAfter", after.hasProperty(SEGMENT)
                    ? after.getValue(SEGMENT).getSerializedName() : "water");
            details.put("waterAfter", after.hasProperty(WATERLOGGED)
                    && after.getValue(WATERLOGGED));
            AlgaeDiagnostics.emit(level, pos, "algae_support", reason, null, before, after, details);
        }

        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
            if (!level.isClientSide) {
                BlockPos above = pos.above();
                while (level.getBlockState(above).is(this)) {
                    BlockState aboveState = level.getBlockState(above);
                    level.setBlock(above, waterState(aboveState), UPDATE_ALL);
                    emitRemoval(level, above, aboveState, player, "column_cascade");
                    if (!player.getAbilities().instabuild) {
                        for (net.minecraft.world.item.ItemStack drop : Block.getDrops(aboveState,
                                (ServerLevel) level, above, null, player, player.getMainHandItem())) {
                            popResource(level, above, drop);
                        }
                    }
                    above = above.above();
                }
            }
            emitRemoval(level, pos, state, player, "player_break");
            super.playerWillDestroy(level, pos, state, player);
        }

        private static void emitRemoval(LevelAccessor level, BlockPos pos, BlockState state,
                                        Player player, String reason) {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("segment", state.getValue(SEGMENT).getSerializedName());
            details.put("age", state.getValue(AGE));
            details.put("itemCount", 1);
            details.put("waterAfter", true);
            AlgaeDiagnostics.emit(level, pos, "algae_remove", reason, player, state,
                    Fluids.WATER.getSource(false).createLegacyBlock(), details);
        }
    }

    public enum AlgaeSegment implements net.minecraft.util.StringRepresentable {
        SINGLE("single"),
        BODY("body"),
        TOP("top");

        private final String name;

        AlgaeSegment(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
