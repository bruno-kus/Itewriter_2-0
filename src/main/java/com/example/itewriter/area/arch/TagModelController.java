package com.example.itewriter.area.arch;

import com.example.itewriter.area.arch.TagModel;

public class TagModelController {
    TagModel model;
    public boolean nextVariation() {
        return model.increment();
    }
    public boolean previousVariation() {
        return model.decrement();
    }
    public TagModelController(TagModel bm) {
        model = bm;
    }


}
