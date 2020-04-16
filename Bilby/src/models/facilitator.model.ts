import { arrayProp, prop, Typegoose, Ref } from "@hasezoey/typegoose";
import { City } from "./city.model";
import { Availability, Unavailability } from "./availability";

export class Facilitator extends Typegoose {

  @prop({ required: false, ref: City })
  public city!: Ref<City>;

  @arrayProp({ required: true, items: String })
  public trained!: string[];

  @prop({ required: false })
  public reliable!: boolean;

  @arrayProp({ required: true, items: Object })
  public availabilities!: Availability[];

  @arrayProp({ required: false, items: Object })
  public specificUnavailabilities!: Unavailability[];

  @arrayProp({ required: true, items: Object })
  public assignedTimes!: Availability[];

  @arrayProp({ required: true, items: Number })
  public maxWS!: number;

}

export const FacilitatorModel = new Facilitator().getModelForClass(Facilitator);
