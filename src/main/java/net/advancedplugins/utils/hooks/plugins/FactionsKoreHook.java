package net.advancedplugins.utils.hooks.plugins;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.expansionstacker.features.MobStackingFeature;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.LivingEntity;


// this hook is technically not used anywhere, but if it is needed it's here haha
public class FactionsKoreHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.FACTIONSKORE.getPluginName();
    }

    public boolean isStackedEntity(LivingEntity entity) {
        if (!isStackingEnabled()) return false;
        return MobStackingFeature.isStackedEntity(entity);
    }

    public boolean isStackingEnabled() {
        FactionsKore fKore = FactionsKore.get();
        if (fKore.featureExists("mob-stacking")) {
            return this.getMobStackingFeature().isOn();
        }
        return false;
    }

    private MobStackingFeature getMobStackingFeature() {
        return FactionsKore.get().getFeature(MobStackingFeature.class);
    }
}
