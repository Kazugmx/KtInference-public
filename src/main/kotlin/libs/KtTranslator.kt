package org.guardmantokai.libs

import ai.djl.modality.cv.Image
import ai.djl.modality.cv.output.BoundingBox
import ai.djl.modality.cv.output.DetectedObjects
import ai.djl.modality.cv.output.Rectangle
import ai.djl.modality.cv.util.NDImageUtils
import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDList
import ai.djl.ndarray.types.DataType
import ai.djl.translate.NoBatchifyTranslator
import ai.djl.translate.TranslatorContext
import java.util.*
import kotlin.math.min

//TODO("fix for NullPointerException")

class KtTranslator:NoBatchifyTranslator<Image,DetectedObjects> {
    private var classes: MutableMap<Int, String>? = null
    private var maxBoxes:Int = 0
    private var threshold:Float = 0F



    override fun processInput(ctx: TranslatorContext, input: Image): NDList {
        var array:NDArray = input.toNDArray(ctx.ndManager,Image.Flag.GRAYSCALE)
        array = NDImageUtils.resize(array,224)
        array = array.toType(DataType.UINT8,true)
        array = array.expandDims(0)
        return NDList(array)
    }

    override fun processOutput(ctx: TranslatorContext, list: NDList): DetectedObjects {
        lateinit var classIds:IntArray
        lateinit var probabilities:FloatArray
        lateinit var boundingBoxes:NDArray
        lateinit var retNames:ArrayList<String>
        lateinit var retProbs:ArrayList<Double>
        lateinit var retBB:ArrayList<BoundingBox>
        var classId:Int
        var probability:Double


        list.forEach{
            println(it.toString())
            if ("detection_boxes" == it.name){
                boundingBoxes = it.get(0)
            }else if("detection_scores" == it.name){
                probabilities = it.get(0).toFloatArray()
            }else if ("detection_classes" == it.name){
                classIds = it.get(0).toType(DataType.FLOAT16,true).toIntArray()
            }
        }


        Objects.requireNonNull(classIds)
        Objects.requireNonNull(probabilities)
        Objects.requireNonNull(boundingBoxes)

        for (i in 0 until min(classIds.size,maxBoxes)){
            classId = classIds[i]
            probability = probabilities[i].toDouble()

            if(classId > 0 && probability > threshold){
                val className = classes!!.getOrDefault(classId,"#$classId")
                val box = boundingBoxes.get(i.toLong()).toFloatArray()
                val yMin = box[0]
                val xMin = box[1]
                val yMax = box[2]
                val xMax = box[3]
                val rect = Rectangle(
                    xMin.toDouble(),
                    yMin.toDouble(),
                    xMax.toDouble()-xMin.toDouble(),
                    yMax.toDouble()-yMin.toDouble())
                retNames.add(className)
                retProbs.add(probability)
                retBB.add(rect)
            }
        }

        return DetectedObjects(retNames,retProbs,retBB)
    }

}