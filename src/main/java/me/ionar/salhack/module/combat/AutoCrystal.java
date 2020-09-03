// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.item.ItemEndCrystal;
import me.earth.phobos.util.BlockUtil;
import java.util.Iterator;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemPickaxe;
import me.earth.phobos.util.DamageUtil;
import net.minecraft.init.Items;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import me.earth.phobos.util.RenderUtil;
import java.awt.Color;
import me.earth.phobos.event.events.Render3DEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import me.earth.phobos.features.modules.misc.Tracker;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.client.CPacketPlayer;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.Timer;
import java.util.Map;
import net.minecraft.entity.Entity;
import java.util.Queue;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class AutoCrystal extends Module
{
    private final Setting<Settings> setting;
    public Setting<Raytrace> raytrace;
    public Setting<Boolean> place;
    public Setting<Integer> placeDelay;
    public Setting<Float> placeRange;
    public Setting<Float> minDamage;
    public Setting<Integer> wasteAmount;
    public Setting<Boolean> wasteMinDmgCount;
    public Setting<Float> facePlace;
    public Setting<Float> placetrace;
    public Setting<Boolean> antiSurround;
    public Setting<Boolean> explode;
    public Setting<Switch> switchMode;
    public Setting<Integer> breakDelay;
    public Setting<Float> breakRange;
    public Setting<Integer> packets;
    public Setting<Float> breaktrace;
    public Setting<Boolean> manual;
    public Setting<Boolean> manualMinDmg;
    public Setting<Integer> manualBreak;
    public Setting<Boolean> sync;
    public Setting<Boolean> render;
    public Setting<Boolean> box;
    public Setting<Boolean> outline;
    public Setting<Boolean> text;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> boxAlpha;
    private final Setting<Float> lineWidth;
    public Setting<Boolean> customOutline;
    private final Setting<Integer> cRed;
    private final Setting<Integer> cGreen;
    private final Setting<Integer> cBlue;
    private final Setting<Integer> cAlpha;
    public Setting<Float> range;
    public Setting<Target> targetMode;
    public Setting<Integer> minArmor;
    private final Setting<Integer> switchCooldown;
    public Setting<AutoSwitch> autoSwitch;
    public Setting<Bind> switchBind;
    public Setting<Boolean> offhandSwitch;
    public Setting<Boolean> switchBack;
    public Setting<Boolean> lethalSwitch;
    public Setting<Boolean> mineSwitch;
    public Setting<Rotate> rotate;
    private final Setting<Integer> eventMode;
    public Setting<Logic> logic;
    public Setting<Boolean> doubleMap;
    public Setting<Boolean> suicide;
    public Setting<Boolean> webAttack;
    public Setting<Boolean> fullCalc;
    public Setting<Boolean> extraSelfCalc;
    public Setting<DamageSync> damageSync;
    public Setting<Integer> damageSyncTime;
    public Setting<Float> dropOff;
    public Setting<Integer> confirm;
    private Queue<Entity> attackList;
    private Map<Entity, Float> crystalMap;
    private final Timer switchTimer;
    private final Timer manualTimer;
    private final Timer breakTimer;
    private final Timer placeTimer;
    private final Timer syncTimer;
    public static EntityPlayer target;
    private Entity efficientTarget;
    private double currentDamage;
    private double renderDamage;
    private double lastDamage;
    private boolean didRotation;
    private boolean switching;
    private BlockPos placePos;
    private BlockPos renderPos;
    private boolean mainHand;
    private boolean rotating;
    private boolean offHand;
    private int crystalCount;
    private int minDmgCount;
    private int lastSlot;
    private float yaw;
    private float pitch;
    private BlockPos webPos;
    private final Timer renderTimer;
    private BlockPos lastPos;
    public static ArrayList<BlockPos> placedPos;
    public static ArrayList<BlockPos> brokenPos;
    private boolean posConfirmed;
    
    public AutoCrystal() {
        super("AutoCrystal", "Best CA on the market", Category.COMBAT, true, false, false);
        this.setting = (Setting<Settings>)this.register(new Setting("Settings", (T)Settings.PLACE));
        this.raytrace = (Setting<Raytrace>)this.register(new Setting("Raytrace", (T)Raytrace.NONE, v -> this.setting.getValue() == Settings.MISC));
        this.place = (Setting<Boolean>)this.register(new Setting("Place", (T)true, v -> this.setting.getValue() == Settings.PLACE));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", (T)0, (T)0, (T)1000, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", (T)6.0f, (T)0.0f, (T)10.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", (T)4.0f, (T)0.1f, (T)20.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteAmount = (Setting<Integer>)this.register(new Setting("WasteAmount", (T)1, (T)1, (T)5, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteMinDmgCount = (Setting<Boolean>)this.register(new Setting("CountMinDmg", (T)true, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.facePlace = (Setting<Float>)this.register(new Setting("FacePlace", (T)8.0f, (T)0.1f, (T)20.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placetrace = (Setting<Float>)this.register(new Setting("Placetrace", (T)6.0f, (T)0.0f, (T)10.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK));
        this.antiSurround = (Setting<Boolean>)this.register(new Setting("AntiSurround", (T)false, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.explode = (Setting<Boolean>)this.register(new Setting("Break", (T)true, v -> this.setting.getValue() == Settings.BREAK));
        this.switchMode = (Setting<Switch>)this.register(new Setting("Attack", (T)Switch.BREAKSLOT, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", (T)0, (T)0, (T)1000, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", (T)6.0f, (T)0.0f, (T)10.0f, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.packets = (Setting<Integer>)this.register(new Setting("Packets", (T)1, (T)1, (T)6, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breaktrace = (Setting<Float>)this.register(new Setting("Breaktrace", (T)6.0f, (T)0.0f, (T)10.0f, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE));
        this.manual = (Setting<Boolean>)this.register(new Setting("Manual", (T)false, v -> this.setting.getValue() == Settings.BREAK));
        this.manualMinDmg = (Setting<Boolean>)this.register(new Setting("ManMinDmg", (T)false, v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.manualBreak = (Setting<Integer>)this.register(new Setting("ManualDelay", (T)500, (T)0, (T)1000, v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.sync = (Setting<Boolean>)this.register(new Setting("Sync", (T)true, v -> this.setting.getValue() == Settings.BREAK && (this.explode.getValue() || this.manual.getValue())));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)true, v -> this.setting.getValue() == Settings.RENDER));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)true, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.text = (Setting<Boolean>)this.register(new Setting("Text", (T)false, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)125, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.5f, (T)0.1f, (T)5.0f, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", (T)false, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)255, (T)0, (T)255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)12.0f, (T)0.1f, (T)20.0f, v -> this.setting.getValue() == Settings.MISC));
        this.targetMode = (Setting<Target>)this.register(new Setting("Target", (T)Target.CLOSEST, v -> this.setting.getValue() == Settings.MISC));
        this.minArmor = (Setting<Integer>)this.register(new Setting("MinArmor", (T)0, (T)0, (T)125, v -> this.setting.getValue() == Settings.MISC));
        this.switchCooldown = (Setting<Integer>)this.register(new Setting("Cooldown", (T)500, (T)0, (T)1000, v -> this.setting.getValue() == Settings.MISC));
        this.autoSwitch = (Setting<AutoSwitch>)this.register(new Setting("Switch", (T)AutoSwitch.TOGGLE, v -> this.setting.getValue() == Settings.MISC));
        this.switchBind = (Setting<Bind>)this.register(new Setting("SwitchBind", (T)new Bind(-1), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE));
        this.offhandSwitch = (Setting<Boolean>)this.register(new Setting("Offhand", (T)false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.switchBack = (Setting<Boolean>)this.register(new Setting("Switchback", (T)false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.offhandSwitch.getValue()));
        this.lethalSwitch = (Setting<Boolean>)this.register(new Setting("LethalSwitch", (T)false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.mineSwitch = (Setting<Boolean>)this.register(new Setting("MineSwitch", (T)false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.rotate = (Setting<Rotate>)this.register(new Setting("Rotate", (T)Rotate.OFF, v -> this.setting.getValue() == Settings.MISC));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", (T)3, (T)1, (T)3, v -> this.setting.getValue() == Settings.MISC));
        this.logic = (Setting<Logic>)this.register(new Setting("Logic", (T)Logic.BREAKPLACE, v -> this.setting.getValue() == Settings.MISC));
        this.doubleMap = (Setting<Boolean>)this.register(new Setting("DoubleMap", (T)false, v -> this.setting.getValue() == Settings.MISC && this.logic.getValue() == Logic.PLACEBREAK));
        this.suicide = (Setting<Boolean>)this.register(new Setting("Suicide", (T)false, v -> this.setting.getValue() == Settings.MISC));
        this.webAttack = (Setting<Boolean>)this.register(new Setting("WebAttack", (T)false, v -> this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE));
        this.fullCalc = (Setting<Boolean>)this.register(new Setting("ExtraCalc", (T)false, v -> this.setting.getValue() == Settings.MISC));
        this.extraSelfCalc = (Setting<Boolean>)this.register(new Setting("MinSelfDmg", (T)true, v -> this.setting.getValue() == Settings.MISC));
        this.damageSync = (Setting<DamageSync>)this.register(new Setting("DamageSync", (T)DamageSync.NONE, v -> this.setting.getValue() == Settings.MISC));
        this.damageSyncTime = (Setting<Integer>)this.register(new Setting("SyncDelay", (T)500, (T)0, (T)1000, v -> this.setting.getValue() == Settings.MISC && this.damageSync.getValue() != DamageSync.NONE));
        this.dropOff = (Setting<Float>)this.register(new Setting("DropOff", (T)5.0f, (T)0.0f, (T)10.0f, v -> this.setting.getValue() == Settings.MISC && this.damageSync.getValue() == DamageSync.BREAK));
        this.confirm = (Setting<Integer>)this.register(new Setting("Confirm", (T)250, (T)0, (T)1000, v -> this.setting.getValue() == Settings.MISC && this.damageSync.getValue() != DamageSync.NONE));
        this.attackList = new ConcurrentLinkedQueue<Entity>();
        this.crystalMap = new HashMap<Entity, Float>();
        this.switchTimer = new Timer();
        this.manualTimer = new Timer();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
        this.syncTimer = new Timer();
        this.efficientTarget = null;
        this.currentDamage = 0.0;
        this.renderDamage = 0.0;
        this.lastDamage = 0.0;
        this.didRotation = false;
        this.switching = false;
        this.placePos = null;
        this.renderPos = null;
        this.mainHand = false;
        this.rotating = false;
        this.offHand = false;
        this.crystalCount = 0;
        this.minDmgCount = 0;
        this.lastSlot = -1;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.webPos = null;
        this.renderTimer = new Timer();
        this.lastPos = null;
        this.posConfirmed = false;
    }
    
    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.doAutoCrystal();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.doAutoCrystal();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.doAutoCrystal();
        }
    }
    
    @Override
    public void onDisable() {
        this.rotating = false;
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "§aSwitch";
        }
        if (AutoCrystal.target != null) {
            return AutoCrystal.target.func_70005_c_();
        }
        return null;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && this.eventMode.getValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = event.getPacket();
            packet.field_149476_e = this.yaw;
            packet.field_149473_f = this.pitch;
            this.rotating = false;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion) {
            final SPacketExplosion packet = event.getPacket();
            final BlockPos pos = new BlockPos(packet.func_149148_f(), packet.func_149143_g(), packet.func_149145_h()).func_177977_b();
            if (this.damageSync.getValue() == DamageSync.PLACE) {
                if (AutoCrystal.placedPos.contains(pos)) {
                    if (!Tracker.getInstance().isOn()) {
                        AutoCrystal.placedPos.remove(pos);
                    }
                    this.posConfirmed = true;
                }
            }
            else if (this.damageSync.getValue() == DamageSync.BREAK && AutoCrystal.brokenPos.contains(pos)) {
                AutoCrystal.brokenPos.remove(pos);
                this.posConfirmed = true;
            }
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && this.render.getValue() && (this.box.getValue() || this.text.getValue() || this.outline.getValue())) {
            RenderUtil.drawBoxESP(this.renderPos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            if (this.text.getValue()) {
                RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
            }
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(AutoCrystal.mc.field_71462_r instanceof PhobosGui) && this.switchBind.getValue().getKey() == Keyboard.getEventKey()) {
            if (this.switchBack.getValue() && this.offhandSwitch.getValue() && this.offHand) {
                final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
                if (module.isOff()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
                }
                else if (module.type.getValue() == Offhand.Type.NEW) {
                    module.setSwapToTotem(true);
                    module.doOffhand();
                }
                else {
                    module.setMode(Offhand.Mode2.TOTEMS);
                    module.doSwitch();
                }
                return;
            }
            this.switching = !this.switching;
        }
    }
    
    private void doAutoCrystal() {
        if (this.check()) {
            switch (this.logic.getValue()) {
                case PLACEBREAK: {
                    this.placeCrystal();
                    if (this.doubleMap.getValue()) {
                        this.mapCrystals();
                    }
                    this.breakCrystal();
                    break;
                }
                case BREAKPLACE: {
                    this.breakCrystal();
                    this.placeCrystal();
                    break;
                }
            }
            this.manualBreaker();
        }
    }
    
    private boolean check() {
        if (fullNullCheck()) {
            return false;
        }
        if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.mainHand = (AutoCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP);
        this.offHand = (AutoCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != AutoCrystal.mc.field_71439_g.field_71071_by.field_70461_c || AutoTrap.isPlacing || Surround.isPlacing) {
            this.lastSlot = AutoCrystal.mc.field_71439_g.field_71071_by.field_70461_c;
            this.switchTimer.reset();
        }
        if (this.offHand || this.mainHand) {
            this.switching = false;
        }
        if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness((EntityPlayer)AutoCrystal.mc.field_71439_g) || !this.switchTimer.passedMs(this.switchCooldown.getValue())) {
            this.renderPos = null;
            AutoCrystal.target = null;
            return this.rotating = false;
        }
        if (this.mineSwitch.getValue() && AutoCrystal.mc.field_71474_y.field_74312_F.func_151470_d() && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && AutoCrystal.mc.field_71474_y.field_74313_G.func_151470_d() && AutoCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) {
            this.switchItem();
        }
        this.mapCrystals();
        if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(this.confirm.getValue())) {
            this.syncTimer.setMs(this.damageSyncTime.getValue() + 1);
        }
        return true;
    }
    
    private void mapCrystals() {
        this.efficientTarget = null;
        if (this.packets.getValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue<Entity>();
            this.crystalMap = new HashMap<Entity, Float>();
        }
        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5f;
        for (final Entity crystal : AutoCrystal.mc.field_71441_e.field_72996_f) {
            if (crystal instanceof EntityEnderCrystal && this.isValid(crystal)) {
                boolean count = false;
                boolean countMin = false;
                final float selfDamage = DamageUtil.calculateDamage(crystal, (Entity)AutoCrystal.mc.field_71439_g);
                if (selfDamage + 0.5 < EntityUtil.getHealth((Entity)AutoCrystal.mc.field_71439_g) || !DamageUtil.canTakeDamage(this.suicide.getValue())) {
                    for (final EntityPlayer player : AutoCrystal.mc.field_71441_e.field_73010_i) {
                        if (player.func_70068_e(crystal) < MathUtil.square(this.range.getValue()) && EntityUtil.isValid((Entity)player, this.range.getValue() + this.breakRange.getValue())) {
                            final float damage = DamageUtil.calculateDamage(crystal, (Entity)player);
                            if (damage <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth((Entity)player)) {
                                continue;
                            }
                            if (damage > maxDamage) {
                                maxDamage = damage;
                                maxCrystal = crystal;
                            }
                            if (this.packets.getValue() == 1) {
                                if (damage >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                                    count = true;
                                }
                                countMin = true;
                            }
                            else {
                                if (this.crystalMap.get(crystal) != null && this.crystalMap.get(crystal) >= damage) {
                                    continue;
                                }
                                this.crystalMap.put(crystal, damage);
                            }
                        }
                    }
                }
                if (!countMin) {
                    continue;
                }
                ++this.minDmgCount;
                if (!count) {
                    continue;
                }
                ++this.crystalCount;
            }
        }
        if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
            this.lastDamage = maxDamage;
        }
        if (this.webAttack.getValue() && this.webPos != null) {
            if (AutoCrystal.mc.field_71439_g.func_174818_b(this.webPos.func_177984_a()) > MathUtil.square(this.breakRange.getValue())) {
                this.webPos = null;
            }
            else {
                for (final Entity entity : AutoCrystal.mc.field_71441_e.func_72872_a((Class)Entity.class, new AxisAlignedBB(this.webPos.func_177984_a()))) {
                    if (entity instanceof EntityEnderCrystal) {
                        this.attackList.add(entity);
                        this.efficientTarget = entity;
                        this.webPos = null;
                        this.lastDamage = 0.5;
                        return;
                    }
                }
            }
        }
        if (this.manual.getValue() && this.manualMinDmg.getValue() && AutoCrystal.mc.field_71474_y.field_74313_G.func_151470_d() && ((this.offHand && AutoCrystal.mc.field_71439_g.func_184600_cs() == EnumHand.OFF_HAND) || (this.mainHand && AutoCrystal.mc.field_71439_g.func_184600_cs() == EnumHand.MAIN_HAND)) && maxDamage < this.minDamage.getValue()) {
            this.efficientTarget = null;
            return;
        }
        if (this.packets.getValue() == 1) {
            this.efficientTarget = maxCrystal;
        }
        else {
            this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
            for (final Map.Entry<Entity, Float> entry : this.crystalMap.entrySet()) {
                final Entity crystal2 = entry.getKey();
                final float damage2 = entry.getValue();
                if (damage2 >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                    ++this.crystalCount;
                }
                this.attackList.add(crystal2);
                ++this.minDmgCount;
            }
        }
    }
    
    private void placeCrystal() {
        int crystalLimit = this.wasteAmount.getValue();
        if (this.placeTimer.passedMs(this.placeDelay.getValue()) && this.place.getValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
            if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!this.antiSurround.getValue() || this.lastPos == null || !this.lastPos.equals((Object)this.placePos))) {
                return;
            }
            this.calculateDamage(this.getTarget(this.targetMode.getValue() == Target.UNSAFE));
            if (AutoCrystal.target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > this.minDamage.getValue() || (this.lethalSwitch.getValue() && EntityUtil.getHealth((Entity)AutoCrystal.target) < this.facePlace.getValue())) && !this.switchItem()) {
                    return;
                }
                if (this.currentDamage < this.minDamage.getValue()) {
                    crystalLimit = 1;
                }
                if ((this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (this.antiSurround.getValue() && this.lastPos != null && this.lastPos.equals((Object)this.placePos))) && (this.currentDamage > this.minDamage.getValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0 && (DamageUtil.isArmorLow(AutoCrystal.target, this.minArmor.getValue()) || EntityUtil.getHealth((Entity)AutoCrystal.target) < this.facePlace.getValue() || this.currentDamage > this.minDamage.getValue())) {
                    final float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (this.dropOff.getValue() - 5.0f) : 0.0f;
                    if (this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE) {
                        if (this.damageSync.getValue() != DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }
                        this.renderPos = this.placePos;
                        this.renderDamage = this.currentDamage;
                        if (this.switchItem()) {
                            this.rotateToPos(this.placePos);
                            BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                            AutoCrystal.placedPos.add(this.placePos);
                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs(this.damageSyncTime.getValue())) {
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            }
            else {
                this.renderPos = null;
            }
        }
    }
    
    private boolean switchItem() {
        if (this.offHand || this.mainHand) {
            return true;
        }
        switch (this.autoSwitch.getValue()) {
            case NONE: {
                return false;
            }
            case TOGGLE: {
                if (!this.switching) {
                    return false;
                }
            }
            case ALWAYS: {
                if (this.doSwitch()) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    private boolean doSwitch() {
        if (!this.offhandSwitch.getValue()) {
            if (AutoCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
                this.mainHand = false;
            }
            else {
                InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
                this.mainHand = true;
            }
            this.switching = false;
            return true;
        }
        final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
        if (module.isOff()) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
            return this.switching = false;
        }
        if (module.type.getValue() == Offhand.Type.NEW) {
            module.setSwapToTotem(false);
            module.setMode(Offhand.Mode.CRYSTALS);
            module.doOffhand();
        }
        else {
            module.setMode(Offhand.Mode2.CRYSTALS);
            module.doSwitch();
        }
        this.switching = false;
        return true;
    }
    
    private void calculateDamage(final EntityPlayer targettedPlayer) {
        if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !this.fullCalc.getValue()) {
            return;
        }
        float maxDamage = 0.5f;
        EntityPlayer currentTarget = null;
        BlockPos currentPos = null;
        float maxSelfDamage = 0.0f;
        BlockPos setToAir = null;
        IBlockState state = null;
        if (this.webAttack.getValue() && targettedPlayer != null) {
            final BlockPos playerPos = new BlockPos(targettedPlayer.func_174791_d());
            final Block web = AutoCrystal.mc.field_71441_e.func_180495_p(playerPos).func_177230_c();
            if (web == Blocks.field_150321_G) {
                setToAir = playerPos;
                state = AutoCrystal.mc.field_71441_e.func_180495_p(playerPos);
                AutoCrystal.mc.field_71441_e.func_175698_g(playerPos);
            }
        }
        for (final BlockPos pos : BlockUtil.possiblePlacePositions(this.placeRange.getValue(), this.antiSurround.getValue())) {
            if (BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && AutoCrystal.mc.field_71439_g.func_174818_b(pos) > MathUtil.square(this.placetrace.getValue()), 1.0f)) {
                float selfDamage = -1.0f;
                if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                    selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.field_71439_g);
                }
                if (selfDamage + 0.5 >= EntityUtil.getHealth((Entity)AutoCrystal.mc.field_71439_g)) {
                    continue;
                }
                if (targettedPlayer != null) {
                    final float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
                    if ((playerDamage <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage <= selfDamage && (playerDamage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage <= EntityUtil.getHealth((Entity)targettedPlayer))) {
                        continue;
                    }
                    maxDamage = playerDamage;
                    currentTarget = targettedPlayer;
                    currentPos = pos;
                    maxSelfDamage = selfDamage;
                }
                else {
                    for (final EntityPlayer player : AutoCrystal.mc.field_71441_e.field_73010_i) {
                        if (EntityUtil.isValid((Entity)player, this.placeRange.getValue() + this.range.getValue())) {
                            final float playerDamage2 = DamageUtil.calculateDamage(pos, (Entity)player);
                            if ((playerDamage2 <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage2 < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage2 <= selfDamage && (playerDamage2 <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage2 <= EntityUtil.getHealth((Entity)player))) {
                                continue;
                            }
                            maxDamage = playerDamage2;
                            currentTarget = player;
                            currentPos = pos;
                            maxSelfDamage = selfDamage;
                        }
                    }
                }
            }
        }
        if (setToAir != null) {
            AutoCrystal.mc.field_71441_e.func_175656_a(setToAir, state);
            this.webPos = currentPos;
        }
        AutoCrystal.target = currentTarget;
        this.currentDamage = maxDamage;
        this.placePos = currentPos;
    }
    
    private EntityPlayer getTarget(final boolean unsafe) {
        if (this.targetMode.getValue() == Target.DAMAGE) {
            return null;
        }
        EntityPlayer currentTarget = null;
        for (final EntityPlayer player : AutoCrystal.mc.field_71441_e.field_73010_i) {
            if (EntityUtil.isntValid((Entity)player, this.placeRange.getValue() + this.range.getValue())) {
                continue;
            }
            if (unsafe && EntityUtil.isSafe((Entity)player)) {
                continue;
            }
            if (this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player, this.minArmor.getValue())) {
                currentTarget = player;
                break;
            }
            if (currentTarget == null) {
                currentTarget = player;
            }
            else {
                if (AutoCrystal.mc.field_71439_g.func_70068_e((Entity)player) >= AutoCrystal.mc.field_71439_g.func_70068_e((Entity)currentTarget)) {
                    continue;
                }
                currentTarget = player;
            }
        }
        if (unsafe && currentTarget == null) {
            return this.getTarget(false);
        }
        return currentTarget;
    }
    
    private void breakCrystal() {
        if (this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (this.packets.getValue() == 1 && this.efficientTarget != null) {
                this.rotateTo(this.efficientTarget);
                EntityUtil.attackEntity(this.efficientTarget, this.sync.getValue(), true);
                AutoCrystal.brokenPos.add(this.efficientTarget.func_180425_c().func_177977_b());
            }
            else if (!this.attackList.isEmpty()) {
                for (int i = 0; i < this.packets.getValue(); ++i) {
                    final Entity entity = this.attackList.poll();
                    if (entity != null) {
                        this.rotateTo(entity);
                        EntityUtil.attackEntity(entity, this.sync.getValue(), true);
                        AutoCrystal.brokenPos.add(entity.func_180425_c().func_177977_b());
                    }
                }
            }
            this.breakTimer.reset();
        }
    }
    
    private void manualBreaker() {
        if (this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() != 2 && this.rotating) {
            if (this.didRotation) {
                final EntityPlayerSP field_71439_g = AutoCrystal.mc.field_71439_g;
                field_71439_g.field_70125_A += (float)4.0E-4;
                this.didRotation = false;
            }
            else {
                final EntityPlayerSP field_71439_g2 = AutoCrystal.mc.field_71439_g;
                field_71439_g2.field_70125_A -= (float)4.0E-4;
                this.didRotation = true;
            }
        }
        if ((this.offHand || this.mainHand) && this.manual.getValue() && this.manualTimer.passedMs(this.manualBreak.getValue()) && AutoCrystal.mc.field_71474_y.field_74313_G.func_151470_d() && AutoCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_151153_ao && AutoCrystal.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151153_ao && AutoCrystal.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151031_f && AutoCrystal.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151062_by) {
            final RayTraceResult result = AutoCrystal.mc.field_71476_x;
            if (result != null) {
                switch (result.field_72313_a) {
                    case ENTITY: {
                        final Entity entity = result.field_72308_g;
                        if (entity instanceof EntityEnderCrystal) {
                            EntityUtil.attackEntity(entity, this.sync.getValue(), true);
                            this.manualTimer.reset();
                            break;
                        }
                        break;
                    }
                    case BLOCK: {
                        final BlockPos mousePos = new BlockPos((double)AutoCrystal.mc.field_71476_x.func_178782_a().func_177958_n(), AutoCrystal.mc.field_71476_x.func_178782_a().func_177956_o() + 1.0, (double)AutoCrystal.mc.field_71476_x.func_178782_a().func_177952_p());
                        for (final Entity target : AutoCrystal.mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(mousePos))) {
                            if (target instanceof EntityEnderCrystal) {
                                EntityUtil.attackEntity(target, this.sync.getValue(), true);
                                this.manualTimer.reset();
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void rotateTo(final Entity entity) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case BREAK:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(AutoCrystal.mc.field_71439_g.func_174824_e(AutoCrystal.mc.func_184121_ak()), entity.func_174791_d());
                if (this.eventMode.getValue() == 2) {
                    Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }
    
    private void rotateToPos(final BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case PLACE:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(AutoCrystal.mc.field_71439_g.func_174824_e(AutoCrystal.mc.func_184121_ak()), new Vec3d((double)(pos.func_177958_n() + 0.5f), (double)(pos.func_177956_o() - 0.5f), (double)(pos.func_177952_p() + 0.5f)));
                if (this.eventMode.getValue() == 2) {
                    Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }
    
    private boolean isValid(final Entity entity) {
        return entity != null && AutoCrystal.mc.field_71439_g.func_70068_e(entity) <= MathUtil.square(this.breakRange.getValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || AutoCrystal.mc.field_71439_g.func_70685_l(entity) || (!AutoCrystal.mc.field_71439_g.func_70685_l(entity) && AutoCrystal.mc.field_71439_g.func_70068_e(entity) <= MathUtil.square(this.breaktrace.getValue())));
    }
    
    static {
        AutoCrystal.target = null;
        AutoCrystal.placedPos = new ArrayList<BlockPos>();
        AutoCrystal.brokenPos = new ArrayList<BlockPos>();
    }
    
    public enum Settings
    {
        PLACE, 
        BREAK, 
        RENDER, 
        MISC;
    }
    
    public enum DamageSync
    {
        NONE, 
        PLACE, 
        BREAK;
    }
    
    public enum Rotate
    {
        OFF, 
        PLACE, 
        BREAK, 
        ALL;
    }
    
    public enum Target
    {
        CLOSEST, 
        UNSAFE, 
        DAMAGE;
    }
    
    public enum Logic
    {
        BREAKPLACE, 
        PLACEBREAK;
    }
    
    public enum Switch
    {
        ALWAYS, 
        BREAKSLOT, 
        CALC;
    }
    
    public enum Raytrace
    {
        NONE, 
        PLACE, 
        BREAK, 
        FULL;
    }
    
    public enum AutoSwitch
    {
        NONE, 
        TOGGLE, 
        ALWAYS;
    }
}
