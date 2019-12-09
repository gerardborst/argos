/*
 * Copyright (C) 2019 Rabobank Nederland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabobank.argos.service.domain.verification;

/*-
 * #%L
 * Argos Supply Chain Notary
 * %%
 * Copyright (C) 2019 Rabobank Nederland
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.rabobank.argos.domain.layout.Step;
import com.rabobank.argos.domain.link.LinkMetaBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequiredNumberOfLinksVerificationTest {

    private static final String STEP_NAME = "stepName";
    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";
    private RequiredNumberOfLinksVerification requiredNumberOfLinksVerification;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VerificationContext context;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Step step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private LinkMetaBlock linkMetaBlock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private LinkMetaBlock linkMetaBlock2;

    @BeforeEach
    void setup() {
        requiredNumberOfLinksVerification = new RequiredNumberOfLinksVerification();

    }

    @Test
    void getPriority() {
        assertThat(requiredNumberOfLinksVerification.getPriority(), is(Verification.Priority.REQUIRED_NUMBER_OF_LINKS));
    }

    @Test
    void verifyWithRequiredNumberOfLinksShouldReturnValid() {
        when(linkMetaBlock.getSignature().getKeyId()).thenReturn(KEY_1);
        when(context.getLayoutMetaBlock().getLayout().getSteps()).thenReturn(singletonList(step));
        when(step.getStepName()).thenReturn(STEP_NAME);
        when(step.getRequiredNumberOfLinks()).thenReturn(1);
        when(context.getLinksByStepName(eq(STEP_NAME))).thenReturn(singletonList(linkMetaBlock));
        VerificationRunResult result = requiredNumberOfLinksVerification.verify(context);
        assertThat(result.isRunIsValid(), is(true));
    }

    @Test
    void verifyWithNoRequiredNumberOfLinksShouldReturnInValid() {
        when(context.getLayoutMetaBlock().getLayout().getSteps()).thenReturn(singletonList(step));
        when(step.getStepName()).thenReturn(STEP_NAME);
        when(step.getRequiredNumberOfLinks()).thenReturn(2);
        when(context.getLinksByStepName(eq(STEP_NAME))).thenReturn(singletonList(linkMetaBlock));
        VerificationRunResult result = requiredNumberOfLinksVerification.verify(context);
        assertThat(result.isRunIsValid(), is(false));
    }

    @Test
    void verifyWithRequiredNumberOfLinksAndUniqueKeysShouldReturnValid() {
        when(linkMetaBlock.getSignature().getKeyId()).thenReturn(KEY_1);
        when(linkMetaBlock2.getSignature().getKeyId()).thenReturn(KEY_2);
        when(context.getLayoutMetaBlock().getLayout().getSteps()).thenReturn(singletonList(step));
        when(step.getStepName()).thenReturn(STEP_NAME);
        when(step.getRequiredNumberOfLinks()).thenReturn(2);
        when(context.getLinksByStepName(eq(STEP_NAME))).thenReturn(asList(linkMetaBlock, linkMetaBlock2));
        VerificationRunResult result = requiredNumberOfLinksVerification.verify(context);
        assertThat(result.isRunIsValid(), is(true));
    }

    @Test
    void verifyWithRequiredNumberOfLinksAndNonUniqueKeysShouldReturnInValid() {
        when(linkMetaBlock.getSignature().getKeyId()).thenReturn(KEY_1);
        when(linkMetaBlock2.getSignature().getKeyId()).thenReturn(KEY_1);
        when(context.getLayoutMetaBlock().getLayout().getSteps()).thenReturn(singletonList(step));
        when(step.getStepName()).thenReturn(STEP_NAME);
        when(step.getRequiredNumberOfLinks()).thenReturn(2);
        when(context.getLinksByStepName(eq(STEP_NAME))).thenReturn(asList(linkMetaBlock, linkMetaBlock2));
        VerificationRunResult result = requiredNumberOfLinksVerification.verify(context);
        assertThat(result.isRunIsValid(), is(false));
    }
}