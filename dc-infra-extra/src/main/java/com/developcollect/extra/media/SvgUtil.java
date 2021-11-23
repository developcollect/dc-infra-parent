package com.developcollect.extra.media;

import cn.hutool.core.util.IdUtil;
import com.developcollect.core.utils.FileUtil;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

import java.io.*;


/**
 * svg相关工具
 * 基于batik
 * @author zak
 */
public class SvgUtil {

    public static File toPng(File svgFile) throws IOException, TranscoderException {
        File pngFile = FileUtil.createTempFile("dc_", "png");
        toPng(new FileInputStream(svgFile), new FileOutputStream(pngFile));
        return pngFile;
    }

    public static void toPng(InputStream in, OutputStream out) throws IOException, TranscoderException {
        transcode(new PNGTranscoder(), in, out);
    }

    public static void toJpg(InputStream in, OutputStream out) throws IOException, TranscoderException {
        transcode(new JPEGTranscoder(), in, out);
    }

    public static void transcode(ImageTranscoder transcoder, InputStream in, OutputStream out) throws IOException, TranscoderException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        Document doc = f.createSVGDocument(IdUtil.fastSimpleUUID(), in);

        TranscoderInput input = new TranscoderInput(doc);
        TranscoderOutput output = new TranscoderOutput(out);
        transcoder.transcode(input, output);
    }
}
