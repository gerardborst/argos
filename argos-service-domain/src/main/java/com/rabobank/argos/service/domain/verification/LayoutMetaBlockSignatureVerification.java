package com.rabobank.argos.service.domain.verification;

import com.rabobank.argos.domain.layout.LayoutMetaBlock;
import com.rabobank.argos.domain.signing.SignatureValidator;
import com.rabobank.argos.service.domain.key.KeyPairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.rabobank.argos.service.domain.verification.Verification.Priority.LAYOUT_METABLOCK_SIGNATURE;


@Component
@RequiredArgsConstructor
public class LayoutMetaBlockSignatureVerification implements Verification {

    private final SignatureValidator signatureValidator;

    private final KeyPairRepository keyPairRepository;

    @Override
    public Priority getPriority() {
        return LAYOUT_METABLOCK_SIGNATURE;
    }

    @Override
    public VerificationRunResult verify(VerificationContext context) {
        return verify(context.getLayoutMetaBlock());
    }

    private VerificationRunResult verify(LayoutMetaBlock layoutMetaBlock) {
        return VerificationRunResult.builder()
                .runIsValid(layoutMetaBlock
                        .getSignatures()
                        .stream()
                        .allMatch(signature -> keyPairRepository.findByKeyId(signature.getKeyId())
                                .map(
                                        keyPair -> signatureValidator
                                                .isValid(layoutMetaBlock.getLayout(), signature.getSignature(), keyPair
                                                        .getPublicKey()))
                                .orElse(false))
                )
                .build();

    }
}