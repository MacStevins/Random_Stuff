
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ZipResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipFile;

@Mixin(ZipResourcePack.class)
public class ZipResourcePackMixin {

	@ModifyVariable(method = "openFile", at = @At("STORE"))
	private ZipFile getZipFile(ZipFile file) {
		
		File unZipDir = new File(MinecraftClient.getInstance().runDirectory, "extractZip" + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss")));
		
		if(!unZipDir.exists())
			unZipDir.mkdirs();
		
		file.entries().asIterator().forEachRemaining((ent) -> {
			
			String entPath = unZipDir.getPath() + File.separator + ent.getName();
			
			if(ent.isDirectory() || entPath.contains("pack."))
				return;
			
			File entFile = new File(entPath);
			
			if(!entFile.getParentFile().exists())
				entFile.getParentFile().mkdirs();
			
			try(InputStream inStream = file.getInputStream(ent);
				FileOutputStream outStream = new FileOutputStream(entPath)) {
				
				int data;
				while((data = inStream.read()) != -1)
					outStream.write(data);
			
			}
			catch(Exception e) {
				
				MinecraftClient.getInstance().scheduleStop();
			
			}
		
		});
		
		return file;
	
	}

}
