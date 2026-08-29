package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.entity.Chapter;
import com.example.mymangaapp.mymangaapp.entity.Page;
import com.example.mymangaapp.mymangaapp.repository.PageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PageService {

    PageRepository pageRepository;

    public List<Page> createPages(Chapter chapter, List<String> pageUrls) {

        List<Page> pages = new ArrayList<>();
        for (int i = 0; i < pageUrls.size(); i++) {
            pages.add(
                    Page.builder()
                            .pageNumber(i + 1)
                            .imageUrl(pageUrls.get(i))
                            .chapter(chapter)
                            .build()
            );
        }

        // Không save, vì chapter entity dùng cascade
        // nên khi chapter service gọi hàm này rồi set cho nó
        // jpa auto save chapter và save cả các page này luôn
        return pages;
    }

}
