package org.guardmantokai

import ai.djl.Application
import ai.djl.Device
import ai.djl.ModelException
import ai.djl.modality.cv.ImageFactory
import ai.djl.modality.cv.output.DetectedObjects
import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.TranslateException
import org.guardmantokai.ObjectDetection.Companion.predict
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import ai.djl.modality.cv.Image as CvImg


fun main() = predict()

class ObjectDetection {
    companion object {
        private val logger:Logger = LoggerFactory.getLogger(ObjectDetection::class.java)


        @Throws(IOException::class, ModelException::class, TranslateException::class)
        fun predict(){
            val a = System.currentTimeMillis()
            val image: CvImg = ImageFactory.getInstance().fromFile(Path.of("build/test.jpg"))

            val criteria:Criteria<CvImg,DetectedObjects> = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(CvImg::class.java,DetectedObjects::class.java)
                .optModelName("ssd_mobilenet_v2_320x320_coco17_tpu-8/saved_model")
                .optArtifactId("ssd")
                .optEngine("TensorFlow")
                .optDevice(Device.cpu())
                .optProgress(ProgressBar())
                .optArgument("backend","aaa")
                .build()

            criteria.loadModel().use { md->
                md.newPredictor().use { predictor ->
                    val detection = predictor.predict(image)
                    saveBoundingBoxImage(image, detection)
                    logger.info("{}",detection)
                }
            }
        }

        private fun saveBoundingBoxImage(img: CvImg, detection: DetectedObjects) {
            val outputDir = Paths.get("build/output")
            Files.createDirectories(outputDir)

            img.drawBoundingBoxes(detection)

            val imagePath = outputDir.resolve("detected.png")
            // OpenJDK can't save jpg with alpha channel
            img.save(Files.newOutputStream(imagePath), "png")
            logger.info("Detected objects image has been saved in: {}", imagePath)
        }
    }
}