import { prop, Typegoose } from "@hasezoey/typegoose";

export class City extends Typegoose {

  @prop({ required: true })
  public city!: string;

}

export const CityModel = new City().getModelForClass(City);
