package com.fwmotion.threescale.cms.matchers;

import com.fwmotion.threescale.cms.model.CmsObject;
import com.fwmotion.threescale.cms.model.CmsSection;
import com.redhat.threescale.rest.cms.model.Section;
import org.hamcrest.Description;

import javax.annotation.Nonnull;

public class CmsSectionMatcher extends CmsObjectMatcher {

    private final Section expected;

    public CmsSectionMatcher(@Nonnull Section expected) {
        super(
            expected.getId(),
            expected.getCreatedAt(),
            expected.getUpdatedAt()
        );
        this.expected = expected;
    }

    @Override
    public boolean matchesSafely(@Nonnull CmsObject actual) {
        if (!(actual instanceof CmsSection)) {
            return false;
        }

        CmsSection actualSection = (CmsSection) actual;

        return super.matchesSafely(actual)
            && actualMatchesExpected(expected.getParentId(), actualSection.getParentId())
            && actualMatchesExpected(expected.getSystemName(), actualSection.getSystemName())
            && actualMatchesExpected(expected.getPartialPath(), actualSection.getPath())
            && actualMatchesExpected(expected.getPublic(), actualSection.getPublic());
    }

    @Override
    public void describeTo(@Nonnull Description description) {
        description.appendText("CmsSection from " + expected);
    }
}
