import * as XLSX from "xlsx";
import { Booking, BookingModel, BookingState } from "../models/booking.model";
import { CityModel, City } from "../models/city.model";
import { UserModel, User, UserType } from "../models/user.model";
import { SchoolModel } from "../models/school.model";
import { FacilitatorModel } from "../models/facilitator.model";
import { GuestSpeakerModel } from "../models/guestSpeaker.model";
import { LocationModel, Location } from "../models/location.model";
import { WorkshopModel, Workshop } from "../models/workshop.model";
import { Availability } from "../models/availability";
import { TeacherModel } from "../models/teacher.model";
import { symlinkSync } from "fs";

/**
 * Function for Getting the date format
 * @param {number} excelDate The date to be converted
 * @returns {Date} converted Date
 */
function convertDate(excelDate: number): Date {
  const date = new Date((excelDate - (25567 + 2)) * 86400 * 1000);
  return new Date(date.getTime() + (date.getTimezoneOffset() * 60 * 1000));
}

/**
 * Function to get the Cities
 * @param {Buffer} file - The excel sheet
 * @returns {City} Cities array
 */
export function getCities(file: Buffer): City[] {
  const wb = XLSX.read(file, { type: "buffer" });
  const s = wb.Sheets["Locations"];
  const myDataCities: any[] = XLSX.utils.sheet_to_json(s);
  const cities: City[] = [];

  Object.values(myDataCities[0]).forEach((city) => {
    cities.push(new CityModel({ city: city }));
  });
  return cities;
}

/**
 * Function for Getting all the Guest Speakers
 *
 * @export
 * @param {Buffer} file The excel sheet
 * @param {Date} from from date
 * @param {Date} to to date
 * @returns {User[]} Users array
 */
export function getGuestSpeakers(file: Buffer, from: Date, to: Date): User[] {
  const wb = XLSX.read(file, { type: "buffer" });
  const u = wb.Sheets["Master availability"];
  const FAndGSO: any[] = XLSX.utils.sheet_to_json(u);
  const GSUsers: User[] = [];
  const morning = 0.333333333; //8am
  const midday = 0.5; //12pm
  const evening = 0.708333333; //5pm

  for (let i = 0; i < Object.keys(FAndGSO).length; i++) {
    const days = [];
    //Get Monday Availabilities
    if (FAndGSO[i]["Mon am"] === "Y" && FAndGSO[i]["Mon pm"] === "Y") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(evening)
      });
    } else if (FAndGSO[i]["Mon am"] === "Y" && FAndGSO[i]["Mon pm"] === "N") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(midday)
      });
    } else if (FAndGSO[i]["Mon am"] === "N" && FAndGSO[i]["Mon pm"] === "Y") {
      days.push({
        availableFrom: convertDate(midday),
        availableUntil: convertDate(evening)
      });
    } else {
      days.push({
        availableFrom: convertDate(NaN),
        availableUntil: convertDate(NaN)
      });
    }
    //Get Tuesday Availabilities
    if (FAndGSO[i]["Tue am"] === "Y" && FAndGSO[i]["Tue pm"] === "Y") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(evening)
      });
    } else if (FAndGSO[i]["Tue am"] === "Y" && FAndGSO[i]["Tue pm"] === "N") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(midday)
      });
    } else if (FAndGSO[i]["Tue am"] === "N" && FAndGSO[i]["Tue pm"] === "Y") {
      days.push({
        availableFrom: convertDate(midday),
        availableUntil: convertDate(evening)
      });
    } else {
      days.push({
        availableFrom: convertDate(NaN),
        availableUntil: convertDate(NaN)
      });
    }
    //Get Wednesday Availabilities
    if (FAndGSO[i]["Wed am"] === "Y" && FAndGSO[i]["Wed pm"] === "Y") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(evening)
      });
    } else if (FAndGSO[i]["Wed am"] === "Y" && FAndGSO[i]["Wed pm"] === "N") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(midday)
      });
    } else if (FAndGSO[i]["Wed am"] === "N" && FAndGSO[i]["Wed pm"] === "Y") {
      days.push({
        availableFrom: convertDate(midday),
        availableUntil: convertDate(evening)
      });
    } else {
      days.push({
        availableFrom: convertDate(NaN),
        availableUntil: convertDate(NaN)
      });
    }
    //Get Thursday Availabilities
    if (FAndGSO[i]["Thu am"] === "Y" && FAndGSO[i]["Thu pm"] === "Y") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(evening)
      });
    } else if (FAndGSO[i]["Thu am"] === "Y" && FAndGSO[i]["Thu pm"] === "N") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(midday)
      });
    } else if (FAndGSO[i]["Thu am"] === "N" && FAndGSO[i]["Thu pm"] === "Y") {
      days.push({
        availableFrom: convertDate(midday),
        availableUntil: convertDate(evening)
      });
    } else {
      days.push({
        availableFrom: convertDate(NaN),
        availableUntil: convertDate(NaN)
      });
    }
    //Get Friday Availabilities
    if (FAndGSO[i]["Fri am"] === "Y" && FAndGSO[i]["Fri pm"] === "Y") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(evening)
      });
    } else if (FAndGSO[i]["Fri am"] === "Y" && FAndGSO[i]["Fri pm"] === "N") {
      days.push({
        availableFrom: convertDate(morning),
        availableUntil: convertDate(midday)
      });
    } else if (FAndGSO[i]["Fri am"] === "N" && FAndGSO[i]["Fri pm"] === "Y") {
      days.push({
        availableFrom: convertDate(midday),
        availableUntil: convertDate(evening)
      });
    } else {
      days.push({
        availableFrom: convertDate(NaN),
        availableUntil: convertDate(NaN)
      });
    }

    const availabilities: Availability[] = [];

    for (let d = new Date(from); d <= to; d.setDate(d.getDate() + 1)) {
      const times = days[d.getDay()];
      const availableFrom = new Date(times.availableFrom);
      const availableUntil = new Date(times.availableUntil);

      if (!isNaN(availableFrom.getTime()) && !isNaN(availableUntil.getTime())) {
        availabilities.push({
          availableFrom: new Date(new Date(d).setHours(availableFrom.getHours(), availableFrom.getMinutes(), availableFrom.getSeconds())),
          availableUntil: new Date(new Date(d).setHours(availableUntil.getHours(), availableUntil.getMinutes(), availableUntil.getSeconds()))
        });
      }
    }

    if (FAndGSO[i]["Staff code"] === "GS") {
      GSUsers.push(new UserModel({
        firstName: FAndGSO[i]["First Name"],
        /* Commented by Nikhil
        lastName: FAndGSO[i]["Last Name"],
        address: FAndGSO[i]["Address"],
        email: FAndGSO[i]["Email"],
        */
        userType: UserType.GUEST_SPEAKER,
        /* Commented by Nikhil
      maxWS add that right now maxamount is true of false ****************
      phoneNumber: FAndGSO[i]["Phone Number"],
      */
        _guestSpeaker: new GuestSpeakerModel({
          /*Commented by Nikhil
         trained: ((FAndGSO[i]["Trained"]) ? FAndGSO[i]["Trained"].split(",") : ""),
         reliable: ((FAndGSO[i]["Reliable"] === "Yes") ? true : false),
         city: new CityModel({
           city: FAndGSO[i]["City"]
         }),

         */

          availabilities: availabilities,
          /*Commented by Nikhil
          specificUnavailabilities: [
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 1 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 1 To"]),
              notes: FAndGSO[i]["Notes 1"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 2 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 2 To"]),
              notes: FAndGSO[i]["Notes 2"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 3 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 3 To"]),
              notes: FAndGSO[i]["Notes 3"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 4 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 4 To"]),
              notes: FAndGSO[i]["Notes 4"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 5 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 5 To"]),
              notes: FAndGSO[i]["Notes 5"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 6 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 6 To"]),
              notes: FAndGSO[i]["Notes 6"],
            }],
            */
          assignedTimes: [],

          /* added by nikhil*/
          P123: ((FAndGSO[i]["P123"]) ? FAndGSO[i]["P123"].split(",") : ""),
          P456: ((FAndGSO[i]["P456"]) ? FAndGSO[i]["P456"].split(",") : ""),
          DHD: ((FAndGSO[i]["DHD"]) ? FAndGSO[i]["DHD"].split(",") : ""),
          HHI: ((FAndGSO[i]["HHI"]) ? FAndGSO[i]["HHI"].split(",") : ""),
          CSE: ((FAndGSO[i]["CSE"]) ? FAndGSO[i]["CSE"].split(",") : ""),
          Pe: ((FAndGSO[i]["Pe"]) ? FAndGSO[i]["Pe"].split(",") : ""),
          DHDe: ((FAndGSO[i]["DHDe"]) ? FAndGSO[i]["DHDe"].split(",") : ""),
          DADe: ((FAndGSO[i]["DADe"]) ? FAndGSO[i]["DADe"].split(",") : ""),
          HHIe: ((FAndGSO[i]["HHIe"]) ? FAndGSO[i]["HHIe"].split(",") : ""),
          CSEe: ((FAndGSO[i]["CSEe"]) ? FAndGSO[i]["CSEe"].split(",") : ""),
          TBIdea: ((FAndGSO[i]["TBIdea"]) ? FAndGSO[i]["TBIdea"].split(",") : ""),
          Ah: ((FAndGSO[i]["Ah"]) ? FAndGSO[i]["Ah"].split(",") : ""),
          C: ((FAndGSO[i]["Y"]) ? FAndGSO[i]["N"].split(",") : "")
        })
      }));
    }
  }
  return GSUsers;
}
/**
 * Function for Getting all the facilitators
 *
 * @export
 * @param {Buffer} file The excel sheet
 * @param {Date} from from date
 * @param {Date} to to date
 * @returns {User[]} Users array
 */
export function getFacilitators(file: Buffer, from: Date, to: Date): User[] {
  const wb = XLSX.read(file, { type: "buffer" });
  const u = wb.Sheets["Master Availability"];
  const FAndGSO: any[] = XLSX.utils.sheet_to_json(u);
  const facilitatorUsers: User[] = [];
  const morning = 0.333333333; //8am
  const midday = 0.5; //12pm
  const evening = 0.708333333; //5pm
  for (let i = 0; i < Object.keys(FAndGSO).length; i++) {
    if (FAndGSO[i]["Staff code"] === "F") {
      const days = [];
      //Get Monday Availabilities
      if (FAndGSO[i]["Mon am"] === "Y" && FAndGSO[i]["Mon pm"] === "Y") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(evening)
        });
      } else if (FAndGSO[i]["Mon am"] === "Y" && FAndGSO[i]["Mon pm"] === "N") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(midday)
        });
      } else if (FAndGSO[i]["Mon am"] === "N" && FAndGSO[i]["Mon pm"] === "Y") {
        days.push({
          availableFrom: convertDate(midday),
          availableUntil: convertDate(evening)
        });
      } else {
        days.push({
          availableFrom: convertDate(NaN),
          availableUntil: convertDate(NaN)
        });
      }
      //Get Tuesday Availabilities
      if (FAndGSO[i]["Tue am"] === "Y" && FAndGSO[i]["Tue pm"] === "Y") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(evening)
        });
      } else if (FAndGSO[i]["Tue am"] === "Y" && FAndGSO[i]["Tue pm"] === "N") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(midday)
        });
      } else if (FAndGSO[i]["Tue am"] === "N" && FAndGSO[i]["Tue pm"] === "Y") {
        days.push({
          availableFrom: convertDate(midday),
          availableUntil: convertDate(evening)
        });
      } else {
        days.push({
          availableFrom: convertDate(NaN),
          availableUntil: convertDate(NaN)
        });
      }
      //Get Wednesday Availabilities
      if (FAndGSO[i]["Wed am"] === "Y" && FAndGSO[i]["Wed pm"] === "Y") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(evening)
        });
      } else if (FAndGSO[i]["Wed am"] === "Y" && FAndGSO[i]["Wed pm"] === "N") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(midday)
        });
      } else if (FAndGSO[i]["Wed am"] === "N" && FAndGSO[i]["Wed pm"] === "Y") {
        days.push({
          availableFrom: convertDate(midday),
          availableUntil: convertDate(evening)
        });
      } else {
        days.push({
          availableFrom: convertDate(NaN),
          availableUntil: convertDate(NaN)
        });
      }
      //Get Thursday Availabilities
      if (FAndGSO[i]["Thu am"] === "Y" && FAndGSO[i]["Thu pm"] === "Y") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(evening)
        });
      } else if (FAndGSO[i]["Thu am"] === "Y" && FAndGSO[i]["Thu pm"] === "N") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(midday)
        });
      } else if (FAndGSO[i]["Thu am"] === "N" && FAndGSO[i]["Thu pm"] === "Y") {
        days.push({
          availableFrom: convertDate(midday),
          availableUntil: convertDate(evening)
        });
      } else {
        days.push({
          availableFrom: convertDate(NaN),
          availableUntil: convertDate(NaN)
        });
      }
      //Get Friday Availabilities
      if (FAndGSO[i]["Fri am"] === "Y" && FAndGSO[i]["Fri pm"] === "Y") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(evening)
        });
      } else if (FAndGSO[i]["Fri am"] === "Y" && FAndGSO[i]["Fri pm"] === "N") {
        days.push({
          availableFrom: convertDate(morning),
          availableUntil: convertDate(midday)
        });
      } else if (FAndGSO[i]["Fri am"] === "N" && FAndGSO[i]["Fri pm"] === "Y") {
        days.push({
          availableFrom: convertDate(midday),
          availableUntil: convertDate(evening)
        });
      } else {
        days.push({
          availableFrom: convertDate(NaN),
          availableUntil: convertDate(NaN)
        });
      }

      const availabilities: Availability[] = [];

      for (let d = new Date(from); d <= to; d.setDate(d.getDate() + 1)) {
        const times = days[d.getDay()];
        const availableFrom = new Date(times.availableFrom);
        const availableUntil = new Date(times.availableUntil);

        if (!isNaN(availableFrom.getTime()) && !isNaN(availableUntil.getTime())) {
          availabilities.push({
            availableFrom: new Date(new Date(d).setHours(availableFrom.getHours(), availableFrom.getMinutes(), availableFrom.getSeconds())),
            availableUntil: new Date(new Date(d).setHours(availableUntil.getHours(), availableUntil.getMinutes(), availableUntil.getSeconds()))
          });
        }
      }

      facilitatorUsers.push(new UserModel({
        firstName: FAndGSO[i]["First Name"],

        /* Commented by Nikhil
        lastName: FAndGSO[i]["Last Name"],
        address: FAndGSO[i]["Address"],
        email: FAndGSO[i]["Email"],
        */
        userType: UserType.FACILITATOR,
        /* Commented by Nikhil
        maxWS add that right now maxamount is true of false ****************
        phoneNumber: FAndGSO[i]["Phone Number"],
        */
        _facilitator: new FacilitatorModel({
          /*Commented by Nikhil
          trained: ((FAndGSO[i]["Trained"]) ? FAndGSO[i]["Trained"].split(",") : ""),
          reliable: ((FAndGSO[i]["Reliable"] === "Yes") ? true : false),
          city: new CityModel({
            city: FAndGSO[i]["City"]
          }),
 
          */
          availabilities: availabilities,
          /*Commented by Nikhil
          specificUnavailabilities: [
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 1 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 1 To"]),
              notes: FAndGSO[i]["Notes 1"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 2 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 2 To"]),
              notes: FAndGSO[i]["Notes 2"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 3 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 3 To"]),
              notes: FAndGSO[i]["Notes 3"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 4 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 4 To"]),
              notes: FAndGSO[i]["Notes 4"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 5 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 5 To"]),
              notes: FAndGSO[i]["Notes 5"],
            },
            {
              notAvailableFrom: convertDate(FAndGSO[i]["Specific Unavailability 6 From"]),
              notAvailableUntil: convertDate(FAndGSO[i]["Specific Unavailability 6 To"]),
              notes: FAndGSO[i]["Notes 6"],
            }], 
            */
          assignedTimes: [],
          /* added by nikhil*/

          P123: ((FAndGSO[i]["P123"]) ? FAndGSO[i]["P123"].split(",") : ""),
          P456: ((FAndGSO[i]["P456"]) ? FAndGSO[i]["P456"].split(",") : ""),
          DHD: ((FAndGSO[i]["DHD"]) ? FAndGSO[i]["DHD"].split(",") : ""),
          HHI: ((FAndGSO[i]["HHI"]) ? FAndGSO[i]["HHI"].split(",") : ""),
          CSE: ((FAndGSO[i]["CSE"]) ? FAndGSO[i]["CSE"].split(",") : ""),
          Pe: ((FAndGSO[i]["Pe"]) ? FAndGSO[i]["Pe"].split(",") : ""),
          DHDe: ((FAndGSO[i]["DHDe"]) ? FAndGSO[i]["DHDe"].split(",") : ""),
          DADe: ((FAndGSO[i]["DADe"]) ? FAndGSO[i]["DADe"].split(",") : ""),
          HHIe: ((FAndGSO[i]["HHIe"]) ? FAndGSO[i]["HHIe"].split(",") : ""),
          CSEe: ((FAndGSO[i]["CSEe"]) ? FAndGSO[i]["CSEe"].split(",") : ""),
          TBIdea: ((FAndGSO[i]["TBIdea"]) ? FAndGSO[i]["TBIdea"].split(",") : ""),
          Ah: ((FAndGSO[i]["Ah"]) ? FAndGSO[i]["Ah"].split(",") : ""),
          C: ((FAndGSO[i]["Y"]) ? FAndGSO[i]["N"].split(",") : "")
        })
      }));
    }
  }
  return facilitatorUsers;
}

/** commented by Nikhil
 * Function for Getting all the School details
 * @param {Buffer} file - The excel sheet
 * @returns {User} Users array
*/

export function getSchools(file: Buffer): User[] {
  const wb = XLSX.read(file, { type: "buffer" });
  const c = wb.Sheets["Contact Information"];
  const contact: any[] = XLSX.utils.sheet_to_json(c, { header: "A" });
  const schools: User[] = [];

  for (let i = 2; i < Object.keys(contact).length; i++) {
    schools.push(new UserModel({
      firstName: contact[i]["A"],
      email: contact[i]["D"],
      phoneNumber: contact[i]["E"],
      userType: UserType.TEACHER,
      _teacher: new TeacherModel({
        school: new SchoolModel({
          city: new CityModel({
            city: contact[i]["B"],
          }),
          name: contact[i]["C"],
        }),
      }),
    }));
  }
  return schools;
}


/**
  * Function to get Workshop types
  * @param {Buffer} file - The excel sheet
  * @returns {Workshop} Workshop array
  */
export function getWorkshopTypes(file: Buffer): Workshop[] {
  const wb = XLSX.read(file, { type: "buffer" });
  const s = wb.Sheets["Workshops"];
  const myDataWorkshops: any[] = XLSX.utils.sheet_to_json(s, { header: "A" });
  const workshops: Workshop[] = [];

  for (let i = 2; i < Object.keys(myDataWorkshops).length; i++) {
    workshops.push(new WorkshopModel({
      workshopName: myDataWorkshops[i]["A"],
      requireFacilitator: (myDataWorkshops[i]["B"]),
      requireGuestSpeaker: (myDataWorkshops[i]["C"])
    }));
  }
  return workshops;
}

/** 
 * Function for Getting all the Booking details
 * @param {Buffer} file - The excel sheet
 * @param {string} cityName - Sheet name
 * @param {Date} fromDate - From Date
 * @param {Date} toDate - Till Date
 * @returns {Booking} booking
*/

export function getBookings(file: Buffer, cityName: string, fromDate: Date, toDate: Date): Booking[] {
  const wb = XLSX.read(file, { type: "buffer" });
  if (wb.Sheets[cityName]) {
    const m = wb.Sheets[cityName];
    const cityObject: any[] = XLSX.utils.sheet_to_json(m, { header: "A" });
    const workshops = getWorkshopTypes(file);
    const booking: Booking[] = [];
    toDate.setDate(toDate.getDate() + 1);
    for (let i = 2; i < Object.keys(cityObject).length; i++) {
      const workshop = workshops.filter(workshop => workshop.workshopName === cityObject[i]["G"]);
      const da = convertDate(cityObject[i]["B"]);
      if (da >= fromDate && da <= toDate) {
        booking.push(new BookingModel({
          state: BookingState.PENDING,
          facilitator: undefined,
          guestSpeaker: undefined,
          sessionTime: {
            timeBegin: new Date(convertDate(cityObject[i]["C"]).setFullYear(da.getFullYear(), da.getMonth(), da.getDate())),
            timeEnd: new Date(convertDate(cityObject[i]["D"]).setFullYear(da.getFullYear(), da.getMonth(), da.getDate()))
          },
          city: new CityModel({
            city: cityName
          }),
          location: new LocationModel({
            name: cityObject[i]["E"],
            capacity: cityObject[i]["H"],
          }),
          workshop: new WorkshopModel({
            workshopName: cityObject[i]["G"],
            requireFacilitator: workshop[0].requireFacilitator,
            requireGuestSpeaker: workshop[0].requireGuestSpeaker
          }),
          level: cityObject[i]["I"],
          teacher: new UserModel({
            email: cityObject[i]["L"],
            firstName: cityObject[i]["J"],
            lastName: cityObject[i]["J"],
            userType: UserType.TEACHER,
            phoneNumber: cityObject[i]["M"],
            _teacher: new TeacherModel({
              school: new SchoolModel({
                name: cityObject[i]["K"],
                city: new CityModel({
                  city: cityName
                })
              })
            })
          }),
          firstTime: false, // Check This ..cant find any first time option in the excel sheet
        }));
      }
    }
    return booking;
  } else {
    return [];
  }
}

/**
 * Function for Getting all the Booking details
 * @param {Booking} b - The Booking array
 * @returns {Buffer} output file
 */
export function printBooking(b: Booking[]): Buffer {
  const sheetName = "Roster";
  const wb = XLSX.utils.book_new();

  /*added by Nikhil */
  const wsData = [
    ["Day", "Date", "Collins Street", "DWH", "Others", "Workshop", "Facil", "GuestSpeaker", "Location", "Pax", "Level",
      "Contact name", "Contact email", "Contact number", "Billable amount", "Comment"],
  ];

  /*"P123","P456", "DHD","HHI","CSE","Pe","DHDe","DADe","HHIe","CSEe","TBIdea","Ah","C",*/
  /*"School","Ret?",*/
  for (let i = 0; i < Object.keys(b).length; i++) {
    b[i].sessionTime.timeBegin.setHours(b[i].sessionTime.timeBegin.getHours());
    b[i].sessionTime.timeEnd.setHours(b[i].sessionTime.timeEnd.getHours());
    const timeBegin = b[i].sessionTime.timeBegin.toLocaleTimeString();
    const timeEnd = b[i].sessionTime.timeEnd.toLocaleTimeString();
    const row: string[] = [];
    /*added by Nikhil */
    row.push(b[i].sessionTime.timeBegin.toLocaleDateString(undefined, { weekday: "long" }));
    row.push(b[i].sessionTime.timeBegin.toLocaleDateString(undefined, { day: "2-digit" }));

    /*added by Nikhil check logic*/
    if (b[i].location instanceof LocationModel) {
      const location = b[i].location as Location;
      row.push(timeBegin);
    } else {
      row.push("", "", "");
    }

    if (b[i].workshop instanceof WorkshopModel) {
      const Workshoptype = b[i].workshop as Workshop;
      row.push(Workshoptype.workshopName);
    } else {
      row.push("");
    }

    if (b[i].facilitator instanceof UserModel) {
      const facilitator = b[i].facilitator as User;
      row.push(facilitator.firstName);
    } else {
      row.push("");
    }

    if (b[i].guestSpeaker instanceof UserModel) {
      const guestSpeaker = b[i].guestSpeaker as User;
      row.push(guestSpeaker.firstName);
    } else {
      row.push("");
    }


    if (b[i].location instanceof LocationModel) {
      const location = b[i].location as Location;
      row.push(location.address);
      if (location.capacity) {
        row.push(location.capacity.toString());
      } else {
        row.push(" ", " ");
      }

      if (b[i].level) {
        row.push(b[i].level);
      } else {
        row.push("");
      }
      /* look into this 
          if (b[i].teacher instanceof SchoolModel) {
            const school = b[i].teacher as User;
            row.push(SchoolModel.name,SchoolModel.extension);
          } else {
            row.push("", "");
          }
          */

      if (b[i].teacher instanceof UserModel) {
        const teacher = b[i].teacher as User;
        row.push(teacher.firstName, teacher.email, teacher.phoneNumber, teacher.billable, teacher.comment);
      } else {
        row.push("", "", "", "", "");
      }

      /*row.push(timeBegin, timeEnd);*/
      wsData.push(row);
    }
    const ws = XLSX.utils.aoa_to_sheet(wsData);
    const wscols = [
      { width: 30 },
      { width: 15 },
      { width: 5 },
      { width: 9 },
      { width: 6 },
      { width: 18 },
      { width: 10 },
      { width: 18 },
      { width: 19 },
      { width: 30 },
      { width: 18 },
      { width: 20 },
      { width: 30 },
      { width: 20 },
      { width: 20 },
      { width: 30 },
    ];
    ws["!cols"] = wscols;
    wb.SheetNames.push(sheetName);
    wb.Sheets[sheetName] = ws;
    const content = XLSX.write(wb, { type: "buffer", bookType: "xlsx", bookSST: false });
    return content;
  }
