import { prop, Typegoose } from "@hasezoey/typegoose";

export class Location extends Typegoose {

  @prop({ required: true })
  public name!: string;

  @prop({ required: true })
  public address!: string;

  @prop({ required: true })
  public capacity!: number;

  @prop({ required: true })
  public disabilityAccess!: boolean;

  @prop({ required: false })
  public facilities?: string;

}
export const LocationModel = new Location().getModelForClass(Location);
