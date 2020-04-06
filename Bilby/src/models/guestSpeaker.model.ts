import { arrayProp, prop, Typegoose, Ref } from "@hasezoey/typegoose";
import { City } from "./city.model";
import { Availability, Unavailability } from "./availability";

export class GuestSpeaker extends Typegoose {

  @prop({ required: true, ref: City })
  public city!: Ref<City>;

  @arrayProp({ required: true, items: String })
  public trained!: string[];

  @prop({ required: true })
  public reliable!: boolean;

  @arrayProp({ required: true, items: Object })
  public availabilities!: Availability[];

  @arrayProp({ required: true, items: Object })
  public specificUnavailabilities!: Unavailability[];

  @arrayProp({ required: true, items: Object })
  public assignedTimes!: Availability[];

  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  /*public trained!: string[];*/ 
  public P123!: string[];

    /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public P456!: string[];

    /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public DHD!: string[];

    /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public HHI!: string[];

  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public CSE!: string[];

  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public Pe!: string[];

  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public DHDe!: string[];

  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public DADe!: string[];
  

    /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public HHIe!: string[];

    /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public CSEe!: string[];
    
  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public TBIdea!: string[];

  /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public Ah!: string[];

    /* added by Nikhil*/
  @arrayProp({ required: true, items: String })
  public C!: string[];
  

}

export const GuestSpeakerModel = new GuestSpeaker().getModelForClass(GuestSpeaker);
