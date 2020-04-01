import { prop, Typegoose, Ref } from "@hasezoey/typegoose";
import { City } from "./city.model";

export class School extends Typegoose {

  @prop({ required: true, ref: City })
  public city!: Ref<City>;

  @prop({ required: true })
  public name!: string;

}

export const SchoolModel = new School().getModelForClass(School);
