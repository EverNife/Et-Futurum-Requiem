package ganymedes01.etfuturum.mixinplugin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import ganymedes01.etfuturum.lib.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class EtFuturumLateMixins implements ILateMixinLoader {
	@Override
	public String getMixinConfig() {
		return "mixins." + Reference.MOD_ID + ".late.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedMods) {
		List<String> mixins = new ArrayList<>();

		if (loadedMods.contains("ExtraUtilities")) {
			mixins.add("extrautilities.EventHandlerServerMixin");
			mixins.add("extrautilities.EventHandlerSiegeMixin");
			mixins.add("extrautilities.ItemDivisionSigilMixin");
		}

		return mixins;
	}
}
