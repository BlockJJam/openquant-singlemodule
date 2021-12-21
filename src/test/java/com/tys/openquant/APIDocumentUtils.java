package com.tys.openquant;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
public interface APIDocumentUtils {
    static OperationRequestPreprocessor getDocumentRequest(){
        return preprocessRequest(
                modifyUris()
                        .scheme("https")
                        .host("docs.api.com")
                        .removePort(),
                prettyPrint()
        );
    }

    static OperationResponsePreprocessor getDocumentResponse(){
        return preprocessResponse(prettyPrint());
    }
}
