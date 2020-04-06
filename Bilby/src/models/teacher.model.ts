import { prop, Typegoose, Ref } from "@hasezoey/typegoose";
import { School } from "./school.model";

export class Teacher extends Typegoose {

    /* added by Nikhil*/
  @prop({ required: false })
  public billable?: string;

    /* added by Nikhil*/
  @prop({ required: false })
  public comment?: string;

  @prop({ required: true, ref: School })
  public school!: Ref<School>;


}

export const TeacherModel = new Teacher().getModelForClass(Teacher);
