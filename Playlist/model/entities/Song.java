package com.app.playlist.model.entities;

import lombok.*;
import org.springframework.hateoas.Link;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    private Integer id;
    private String name;
    private String selfLink;
}