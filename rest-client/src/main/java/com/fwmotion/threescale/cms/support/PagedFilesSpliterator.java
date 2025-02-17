package com.fwmotion.threescale.cms.support;

import com.fwmotion.threescale.cms.mappers.CmsFileMapper;
import com.fwmotion.threescale.cms.model.CmsFile;
import com.redhat.threescale.rest.cms.ApiException;
import com.redhat.threescale.rest.cms.api.FilesApi;
import com.redhat.threescale.rest.cms.model.FileList;
import org.apache.commons.collections4.ListUtils;
import org.mapstruct.factory.Mappers;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PagedFilesSpliterator extends AbstractPagedRestApiSpliterator<CmsFile> {

    private static final CmsFileMapper FILE_MAPPER = Mappers.getMapper(CmsFileMapper.class);

    private final FilesApi filesApi;

    public PagedFilesSpliterator(@Nonnull FilesApi filesApi) {
        super(Collections.emptySet(), 0);
        this.filesApi = filesApi;
    }

    public PagedFilesSpliterator(@Nonnull FilesApi filesApi,
                                 @Nonnegative int requestedPageSize) {
        super(requestedPageSize, Collections.emptySet(), 0);
        this.filesApi = filesApi;
    }

    private PagedFilesSpliterator(@Nonnull FilesApi filesApi,
                                  @Nonnegative int requestedPageSize,
                                  @Nonnull Collection<CmsFile> currentPage,
                                  @Nonnegative int currentPageNumber) {
        super(requestedPageSize, currentPage, currentPageNumber);
        this.filesApi = filesApi;
    }

    @Nullable
    @Override
    protected Collection<CmsFile> getPage(@Nonnegative int pageNumber,
                                          @Nonnegative int pageSize) {

        try {
            FileList fileList = filesApi.listFiles(pageNumber, pageSize, null);

            List<CmsFile> resultPage = ListUtils.emptyIfNull(fileList.getFiles())
                .stream()
                .map(FILE_MAPPER::fromRest)
                .collect(Collectors.toList());

            validateResultPageSize(
                "file",
                pageNumber,
                pageSize,
                resultPage,
                fileList::getCurrentPage,
                fileList::getTotalPages,
                fileList::getPerPage,
                fileList::getTotalEntries);

            return resultPage;
        } catch (ApiException e) {
            // TODO: Create ThreescaleCmsException and throw it instead of IllegalStateException
            throw new IllegalStateException("Unexpected exception while iterating CMS file page " + pageNumber
                + " (with page size of " + pageSize + ")", e);
        }
    }

    @Nonnull
    @Override
    protected AbstractPagedRestApiSpliterator<CmsFile> doSplit(
        @Nonnegative int requestedPageSize,
        @Nonnull Collection<CmsFile> currentPage,
        @Nonnegative int currentPageNumber) {
        return new PagedFilesSpliterator(filesApi, requestedPageSize, currentPage, currentPageNumber);
    }

    @Override
    public int characteristics() {
        return Spliterator.DISTINCT |
            Spliterator.SORTED |
            Spliterator.ORDERED |
            Spliterator.NONNULL |
            Spliterator.IMMUTABLE;
    }

    @Nonnull
    @Override
    public Comparator<? super CmsFile> getComparator() {
        return Comparator.comparing(CmsFile::getId);
    }
}
