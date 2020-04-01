import { prop, Typegoose } from "@hasezoey/typegoose";

export class Workshop extends Typegoose {

  @prop({ required: true })
  public workshopName!: string;

  @prop({ required: true })
  public requireFacilitator!: boolean;

  @prop({ required: true })
  public requireGuestSpeaker!: boolean;

}

export const WorkshopModel = new Workshop().getModelForClass(Workshop);
