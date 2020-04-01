import rosterByPreferences from "../src/controllers/rosterAlg";
import { CityModel } from "../src/models/city.model";
import { SchoolModel } from "../src/models/school.model";
import { User, UserModel, UserType } from "../src/models/user.model";
import { GuestSpeakerModel } from "../src/models/guestSpeaker.model";
import { Facilitator, FacilitatorModel } from "../src/models/facilitator.model";
import { LocationModel } from "../src/models/location.model";
import { WorkshopModel } from "../src/models/workshop.model";
import { BookingModel, BookingState } from "../src/models/booking.model";
import { TeacherModel } from "../src/models/teacher.model";
import { NA_GUESTSPEAKER } from "../src/controllers/userOperations";

// We want to pass in all the PENDING bookings
// rosterByPreferences(teacherPreferences, guestSpeakers:, facilitators, locations)
const cities = [new CityModel({ city: "Melbourne" }), new CityModel({ city: "Sydney" }), new CityModel({ city: "Canberra" })];

const schools = [new SchoolModel({
  city: cities[0],
  name: "City Cite - Braybrook SC"
}), new SchoolModel({
  city: cities[0],
  name: "Doxa - Lumen Christi School"
}), new SchoolModel({
  city: cities[0],
  name: "Peninsula Grammar"
})];

const teachers = [new UserModel({
  firstName: "Andrew",
  lastName: "Barker",
  email: "abarker@citycite.vic.edu.au",
  address: "31 Test Drive Avenue",
  userType: UserType.TEACHER,
  phoneNumber: "86606702",
  _teacher: new TeacherModel({
    school: schools[0]
  })
}), new UserModel({
  firstName: "Tenille",
  lastName: "McInerney",
  email: "tmcinerney@doxa.org.au",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "90468200",
  _teacher: new TeacherModel({
    school: schools[1]
  })
})];

const facilitators = [new UserModel({
  firstName: "Phil",
  lastName: "",
  email: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: ["Discussions of Homelessness and Disadvantage", "Finding belonging"],
    reliable: true,
    availabilities: [{
      availableFrom: new Date(2018, 8, 6, 10, 0),
      availableUntil: new Date(2018, 8, 6, 12, 0)
    }],
    specificUnavailabilities: [{
      notAvailableFrom: new Date(2018, 8, 7, 11, 0),
      notAvailableUntil: new Date(2018, 8, 10, 11, 0),
      notes: "On holiday"
    }],
    assignedTimes: []
  })
})];

const guestSpeakers = [new UserModel({
  firstName: "Pete",
  lastName: "B",
  email: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: ["Discussions of Homelessness and Disadvantage", "Finding belonging"],
    reliable: true,
    availabilities: [{
      availableFrom: new Date(2018, 8, 6, 10, 0),
      availableUntil: new Date(2018, 8, 6, 12, 0)
    }],
    specificUnavailabilities: [{
      notAvailableFrom: new Date(2018, 8, 7, 11, 0),
      notAvailableUntil: new Date(2018, 8, 10, 11, 0),
      notes: "On holiday"
    }],
    assignedTimes: []
  })
})];

const locations = [new LocationModel({
  name: "DWH",
  address: " ",
  capacity: 30,
  disabilityAccess: true,
  facilities: "projector",
})];

const workshops = [new WorkshopModel({
  workshopName: "Discussions of Homelessness and Disadvantage",
  requireFacilitator: true,
  requireGuestSpeaker: true,
}), new WorkshopModel({
  workshopName: "Finding belonging",
  requireFacilitator: true,
  requireGuestSpeaker: false,
}), new WorkshopModel({
  workshopName: "Big Idea Lecture",
  requireFacilitator: true,
  requireGuestSpeaker: true,
})];

const bookings = [new BookingModel({
  state: BookingState.PENDING,
  sessionTime: { timeBegin: new Date(2018, 8, 6, 11, 0), timeEnd: new Date(2018, 8, 6, 12, 0) },
  city: cities[0],
  location: locations[0],
  workshop: workshops[1],
  level: "9",
  teacher: teachers[0],
  firstTime: true,
  numberOfStudents: 25
})];

test("workshop does not require guest speaker", () => {
  const bookingsResult = rosterByPreferences(bookings, guestSpeakers, facilitators);

  expect(bookingsResult[0].facilitator).toMatchObject(facilitators[0]);
  expect(bookingsResult[0].guestSpeaker).toMatchObject(NA_GUESTSPEAKER);

  if (bookingsResult[0].facilitator instanceof UserModel) {
    const user = bookingsResult[0].facilitator as User;
    if (user._facilitator instanceof FacilitatorModel) {
      const facilitator = user._facilitator as Facilitator;
      expect(facilitator.assignedTimes[0].availableFrom).toMatchObject(bookingsResult[0].sessionTime.timeBegin);
      expect(facilitator.assignedTimes[0].availableUntil).toMatchObject(bookingsResult[0].sessionTime.timeEnd);
    }
  }
});
