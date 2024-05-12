package dev.roanoke.particletrails.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.roanoke.particletrails.ParticleTrails
import net.fabricmc.loader.api.FabricLoader
import org.joml.Vector3f
import java.io.FileWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Path

class Config {

    init {
        createFolders()
        loadPacks()
    }

    private fun createFolders() {
        var folder = FabricLoader.getInstance().configDir.resolve("ParticleTrails")
        createFile(folder, true)

        folder = folder.resolve("packs")
        createFile(folder, true)
        val filePath = folder.resolve("sample_pack.json")
        if (createFile(filePath)) {
            val file = filePath.toFile()
            FileWriter(file).use {
                GsonBuilder().setPrettyPrinting().create().toJson(loadSamplePack(), it)
            }
        }
    }

    private fun loadSamplePack(): JsonObject? {
        val jsonStream: InputStream? = ParticleTrails::class.java.getResourceAsStream("/sample_pack.json")
        return jsonStream?.use {
            InputStreamReader(it).use { reader ->
                JsonParser.parseReader(reader).asJsonObject
            }
        }
    }

    fun loadPacks() {
        ParticleTrails.trailPacks.clear()
        val folder = FabricLoader.getInstance().configDir.resolve("ParticleTrails/packs")
        val files = folder.toFile().listFiles()
        if (files != null) {
            for (file in files) {
                val trailList: MutableList<Trail> = mutableListOf()
                val json = JsonParser.parseReader(file.reader()).asJsonObject
                val packId = json["name"].asString
                val packDisplay = json["display"].asString
                val packDescription = json["description"].asString
                val trails = json["trails"].asJsonArray
                for (trailElement in trails) {
                    val trail = trailElement.asJsonObject
                    val trailName = trail["name"].asString
                    val trailDisplay = trail["display"].asString
                    val trailDescription = trail["description"].asString
                    val trailType = trail["trailType"].asString
                    val particleType = trail["particleType"].asString
                    val colour = if (trail.has("colour")) {
                        val array = trail["colour"].asJsonArray
                        Vector3f(array[0].asFloat/255f, array[1].asFloat/255f, array[2].asFloat/255f)
                    } else
                        null
                    trailList.add(Trail(trailName, trailDisplay, trailDescription, TrailType.fromString(trailType), particleType, colour, packId))
                }
                ParticleTrails.trailPacks.add(TrailPack(packId, packDescription, packDisplay, trailList))
            }
        }

        val trailList: MutableList<Trail> = mutableListOf()
        val trailTypes = TrailType.values()
        for (particles in Utils.getAllParticleTypes())
            for (type in trailTypes)
                trailList.add(Trail("${type}_${particles.value}", "$type ${particles.value}", "", type, particles.value, null, "ADMIN_PACK"))
        ParticleTrails.trailPacks.add(TrailPack("ADMIN_PACK", "All Available Trails", "<bold><light_purple>ADMIN PACK", trailList))
    }

    private fun createFile(filePath: Path, folder: Boolean = false): Boolean {
        if (!filePath.toFile().exists()) {
            return if (!folder)
                filePath.toFile().createNewFile()
            else
                filePath.toFile().mkdir()
        }
        return false
    }

}