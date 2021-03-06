package com.ecchilon.sadpanda.overview;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Alex on 21-9-2014.
 */
@Data
@Accessors(chain = true)
public class GalleryEntry {
    private Long galleryId;
    private String token;
    private String title;
    private String title_jpn;
    private Category category;
    private String thumb;
    private String uploader;
    private Integer fileCount;
    private Long filesize;
    private boolean expunged;
    private Float rating;
    private int torrentcount;
    private String[] tags;
    private String showkey;
    private Date created;
}
