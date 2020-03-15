package net.haesleinhuepf.clijx.weka;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

/**
 * Author: @haesleinhuepf
 *         November 2019
 */

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_applyOCLWekaModel")
public class ApplyOCLWekaModel extends AbstractCLIJxPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, HasAuthor, HasLicense {

    @Override
    public boolean executeCL() {
        return applyOCLWekaModel(getCLIJx(), (ClearCLBuffer)( args[0]), (ClearCLBuffer)(args[1]), (String)args[2]);
    }

    public static boolean applyOCLWekaModel(CLIJx clijx, ClearCLBuffer srcFeatureStack3D, ClearCLBuffer dstClassificationResult, String loadModelFilename) {
        if (new File(loadModelFilename + ".cl").exists()) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("src_featureStack", srcFeatureStack3D);
            parameters.put("dst", dstClassificationResult);
            parameters.put("export_probabilities", 0);
            clijx.execute(Object.class,loadModelFilename + ".cl", "classify_feature_stack", dstClassificationResult.getDimensions(), dstClassificationResult.getDimensions(), parameters);
        } else {
            new IllegalArgumentException("This model hasn't been saved as OCL Model. Try applyWekaModel instead.");
        }

        //CLIJxWeka weka = new CLIJxWeka(clijx, srcFeatureStack3D, loadModelFilename);
        //String ocl = weka.getOCL();



        //ClearCLBuffer classification = weka.getClassification();
        //clijx.copy(classification, dstClassificationResult);
        //clijx.release(classification);
        return true;
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJx().create(new long[]{input.getWidth(), input.getHeight()}, NativeTypeEnum.Float);
    }

    @Override
    public String getParameterHelpText() {
        return "Image featureStack3D, Image prediction2D_destination, String loadModelFilename";
    }

    @Override
    public String getDescription() {
        return "Applies a Weka model which was saved as OpenCL file. Train your model with trainWekaModel to save it as OpenCL file.\n" +
                "It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)" +
                "and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the same" +
                "way as for training the model!";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D";
    }

    @Override
    public String getAuthorName() {
        return "Robert Haase (rhaase@mpi-cbg.de, based on work by \n" +
                " Verena Kaynig (verena.kaynig@inf.ethz.ch), \n" +
                " Ignacio Arganda-Carreras (iarganda@mit.edu),\n" +
                " Albert Cardona (acardona@ini.phys.ethz.ch))";
    }

    @Override
    public String getLicense() {
        return "Parts of the code of CLIJ Weka were copied over from the Trainable_Segmentation repository (link above). Thus,\n" +
                " this code is licensed GPL2 as well.\n" +
                "\n" +
                "  License: GPL\n" +
                "\n" +
                "  This program is free software; you can redistribute it and/or\n" +
                "  modify it under the terms of the GNU General Public License 2\n" +
                "  as published by the Free Software Foundation.\n" +
                "\n" +
                "  This program is distributed in the hope that it will be useful,\n" +
                "  but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "  GNU General Public License for more details.\n" +
                "\n" +
                "  You should have received a copy of the GNU General Public License\n" +
                "  along with this program; if not, write to the Free Software\n" +
                "  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.\n" +
                "  Authors: Verena Kaynig (verena.kaynig@inf.ethz.ch), Ignacio Arganda-Carreras (iarganda@mit.edu)\n" +
                "           Albert Cardona (acardona@ini.phys.ethz.ch)";
    }
}
