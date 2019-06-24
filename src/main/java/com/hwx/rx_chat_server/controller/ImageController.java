package com.hwx.rx_chat_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController
public class ImageController {

    @Autowired
    private Environment environment;

    @GetMapping("/api/image/{imageId}")
    public StreamingResponseBody getProfileImageData(
              @PathVariable String imageId
            , HttpServletResponse response
    ) {
        try {

//          String uploadRootPath = request.getServletContext().getRealPath("upload");
            String uploadRootPath = environment.getProperty("server.context.upload.path");
            File uploadRootDir = new File(uploadRootPath);

            File picFile = new File(uploadRootDir.getAbsolutePath() + File.separator + imageId);


            if (picFile.exists()) {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + imageId + "\"");

                InputStream inputStream = new FileInputStream(picFile);
                return outputStream -> {
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        //System.out.println("Writing some bytes of file...");
                        outputStream.write(data, 0, nRead);
                    }
                };
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }
}
