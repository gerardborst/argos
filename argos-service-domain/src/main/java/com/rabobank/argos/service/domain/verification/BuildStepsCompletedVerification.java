package com.rabobank.argos.service.domain.verification;

import com.rabobank.argos.domain.layout.LayoutMetaBlock;
import com.rabobank.argos.domain.layout.Step;
import com.rabobank.argos.domain.link.Link;
import com.rabobank.argos.domain.link.LinkMetaBlock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.rabobank.argos.service.domain.verification.Verification.Priority.BUILDSTEPS_COMPLETED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
@Slf4j
public class BuildStepsCompletedVerification implements Verification {
    @Override
    public Priority getPriority() {
        return BUILDSTEPS_COMPLETED;
    }

    @Override
    public VerificationRunResult verify(VerificationContext context) {
        return verifySteps(context.getLinkMetaBlocks(), context.getLayoutMetaBlock());
    }

    private VerificationRunResult verifySteps(List<LinkMetaBlock> linkMetaBlocks, LayoutMetaBlock layoutMetaBlock) {
        Set<String> linkBuildSteps = linkMetaBlocks.stream()
                .map(LinkMetaBlock::getLink).map(Link::getStepName).collect(toSet());

        List<String> expectedSteps = layoutMetaBlock.getLayout().getSteps().stream()
                .map(Step::getStepName).collect(toList());

        log.info("linkBuildSteps: {} , expectedSteps: {}", linkBuildSteps, expectedSteps);

        return VerificationRunResult.builder().runIsValid(
                linkBuildSteps.size() == expectedSteps.size() && expectedSteps.containsAll(linkBuildSteps))
                .build();
    }
}