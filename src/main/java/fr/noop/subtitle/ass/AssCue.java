package fr.noop.subtitle.ass;

import fr.noop.subtitle.base.BaseSubtitleCue;
import fr.noop.subtitle.model.SubtitleRegionCue;
import fr.noop.subtitle.util.SubtitleRegion;

public class AssCue extends BaseSubtitleCue implements SubtitleRegionCue {
  private SubtitleRegion region;

  public void setRegion(SubtitleRegion region) {
    this.region = region;
  }

  @Override
  public SubtitleRegion getRegion() {
      return this.region;
  }
}
