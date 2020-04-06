import { prop, Typegoose, Ref } from "@hasezoey/typegoose";
import { Facilitator } from "./facilitator.model";
import { GuestSpeaker } from "./guestSpeaker.model";
import { Teacher } from "./teacher.model";

export enum UserType {
  FACILITATOR = "facilitator",
  GUEST_SPEAKER = "guest_speaker",
  TEACHER = "teacher",
  COORDINATOR = "coordinator"
}

export class User extends Typegoose {

  /* added by Nikhil*/
  @prop({ required: false, index: true, unique: true, validate: /\S+@\S+\.\S+/ })
  public email!: string;

  @prop({ required: true })
  public firstName!: string;

  /* added by Nikhil*/
  @prop({ required: false })
  public lastName!: string;

  @prop({ required: false })
  public address?: string;



  @prop({ required: true, enum: UserType })
  public userType!: UserType;

  /* added by Nikhil*/
  @prop({ required: false })
  public phoneNumber!: string;



  @prop({ ref: Facilitator })
  public _facilitator?: Ref<Facilitator>;

  @prop({ ref: GuestSpeaker })
  public _guestSpeaker?: Ref<GuestSpeaker>;

  @prop({ ref: Teacher })
  public _teacher?: Ref<Teacher>;
  billable: string;
  comment: string;

}

export const UserModel = new User().getModelForClass(User);
