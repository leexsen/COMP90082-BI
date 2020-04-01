import { prop, Typegoose, Ref } from "@hasezoey/typegoose";
import { Workshop } from "./workshop.model";
import { Location } from "./location.model";
import { User } from "./user.model";
import { City } from "./city.model";

export interface SessionTime {
  timeBegin: Date,
  timeEnd: Date,
}

export enum BookingState {
  CONFIRMED = "confirmed", // Assigned and confirmed
  UNCONFIRMED = "unconfirmed", // Assigned, unconfirmed by coordinator
  PENDING = "pending", // Pending roster
}

export class Booking extends Typegoose {

  @prop({ required: true })
  public state!: BookingState;

  @prop({ required: false, ref: User })
  public facilitator?: Ref<User>;

  @prop({ required: false, ref: User })
  public guestSpeaker?: Ref<User>;

  @prop({ required: true })
  public sessionTime!: SessionTime;

  @prop({ required: true, ref: City })
  public city!: Ref<City>;

  @prop({ required: true, ref: Location })
  public location!: Ref<Location>;

  @prop({ required: true, ref: Workshop })
  public workshop!: Ref<Workshop>;

  @prop({ required: true })
  public level!: string;

  @prop({ required: true, ref: User })
  public teacher!: Ref<User>;

  @prop({ required: true })
  public firstTime!: boolean;

  @prop({ required: true })
  public numberOfStudents!: number;

}

export const BookingModel = new Booking().getModelForClass(Booking);
