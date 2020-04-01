import { Request, Response } from "express";
import stream from "stream";
import rosterByPreferences from "./rosterAlg";
import { getCities, getBookings, getGuestSpeakers, getFacilitators, printBooking } from "./excelOperations";
import { Booking } from "../models/booking.model";

/**
 * Roster a file between two Dates
 *
 * @export
 * @param {Date} from date to roster from
 * @param {Date} to date to roster to
 * @param {Buffer} file excel file to roster
 * @returns {Buffer} output file
 */
export function rosterFile(from: Date, to: Date, file: Buffer): Buffer | null {
  try {
    const cities = getCities(file);
    let bookings: Booking[] = [];
    cities.forEach(city => {
      bookings = bookings.concat(getBookings(file, city.city, new Date(from), new Date(to)));
    });
    const guestSpeakers = getGuestSpeakers(file, new Date(from), new Date(to));
    const facilitators = getFacilitators(file, new Date(from), new Date(to));
    const roster = rosterByPreferences(bookings, guestSpeakers, facilitators);
    return printBooking(roster);
  } catch {
    return null;
  }
}

/**
 * Function to handle uploading an excel file and generate a roster.
 *
 * @export
 * @param {Request} req request object
 * @param {Response} res response object
 * @returns {void}
 */
export function upload(req: Request, res: Response): any {
  if (!req.files || Object.keys(req.files).length === 0) {
    return res.status(400).send("No files were uploaded.");
  }

  if (Array.isArray(req.files.excel)) {
    return res.status(400).send("Only one file is allowed to be uploaded.");
  }

  let from = new Date(req.body.from);
  let to = new Date(req.body.to);

  from = new Date(from.getTime() + (from.getTimezoneOffset() * 60 * 1000));
  to = new Date(to.getTime() + (to.getTimezoneOffset() * 60 * 1000));

  const file = req.files.excel.data;
  const out = rosterFile(from, to, file);

  if (out === null) {
    res.status(400).send("File does not follow specified format.");
    return;
  }

  const readStream = new stream.PassThrough();
  readStream.end(out);

  res.set("Content-disposition", `attachment; filename=roster_${req.body.from}_${req.body.to}.xlsx`);
  res.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  readStream.pipe(res);
}
