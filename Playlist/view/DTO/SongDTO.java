package com.app.playlist.view.DTO;

import lombok.*;
import org.springframework.hateoas.Link;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SongDTO {
    private String name;
    private String selfLink;
}
