import { CityModel } from "../../src/models/city.model";
import { WorkshopModel } from "../../src/models/workshop.model";
import { UserModel, UserType } from "../../src/models/user.model";
import { FacilitatorModel, } from "../../src/models/facilitator.model";
import { TeacherModel } from "../../src/models/teacher.model";
import { SchoolModel } from "../../src/models/school.model";
import { BookingState, BookingModel } from "../../src/models/booking.model";
import { LocationModel } from "../../src/models/location.model";
import { GuestSpeakerModel } from "../../src/models/guestSpeaker.model";
import { dayOfWeek } from "../../src/models/availability";

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
}), new SchoolModel({
  city: cities[0],
  name: "Noble Park Secondary College"
}), new SchoolModel({
  city: cities[0],
  name: "Mount Clear College"
}), new SchoolModel({
  city: cities[0],
  name: "Carey Baptist Grammar"
}), new SchoolModel({
  city: cities[0],
  name: "Buxton Primary School"
}), new SchoolModel({
  city: cities[0],
  name: "Melbourne Uni – Big Idea"
}), new SchoolModel({
  city: cities[0],
  name: "St Francis Xavier College"
}), new SchoolModel({
  city: cities[0],
  name: "Eltham High School"
}), new SchoolModel({
  city: cities[0],
  name: "Whittlesea Secondary College"
}), new SchoolModel({
  city: cities[0],
  name: "Albert Park College"
}), new SchoolModel({
  city: cities[0],
  name: "Copperfield College"
}), new SchoolModel({
  city: cities[0],
  name: "Mentone Grammar"
}), new SchoolModel({
  city: cities[0],
  name: "Williamstown High School"
}), new SchoolModel({
  city: cities[0],
  name: "Lakeview Senior College"
})];
const users = [new UserModel({
  firstName: "Andrew",
  lastName: "Barker",
  email: "abarker@citycite.vic.edu.au",
  passwordHash: "",
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
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "90468200",
  _teacher: new TeacherModel({
    school: schools[1]
  })
}), new UserModel({
  firstName: "Lorraine",
  lastName: "Hickey",
  email: "lhickey@peninsulagrammar.vic.edu.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "97887664",
  _teacher: new TeacherModel({
    school: schools[2]
  })
}), new UserModel({
  firstName: "Trevor",
  lastName: "Barry",
  email: "barry.trevor.m@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "0423624108",
  _teacher: new TeacherModel({
    school: schools[3]
  })
}), new UserModel({
  firstName: "Cassandra",
  lastName: "Bawden",
  email: "bawden.cassandra.j@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "0423737490",
  _teacher: new TeacherModel({
    school: schools[4]
  })
}), new UserModel({
  firstName: "Geoff",
  lastName: "Trevaskis",
  email: "geoff.trevaskis@carey.com.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "9816 1563",
  _teacher: new TeacherModel({
    school: schools[5]
  })
}), new UserModel({
  firstName: "Andrew",
  lastName: "Bagnall",
  email: "bagnall.andrew.a@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "0431277766",
  _teacher: new TeacherModel({
    school: schools[6]
  })
}), new UserModel({
  firstName: "Karina",
  lastName: "Jenkin",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "",
  _teacher: new TeacherModel({
    school: schools[7]
  })
}), new UserModel({
  firstName: "Kerry",
  lastName: "Little",
  email: "klittle@sfx.vic.edu.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "0417108341",
  _teacher: new TeacherModel({
    school: schools[8]
  })
}), new UserModel({
  firstName: "Melissa",
  lastName: "Hughes",
  email: "huh@elthamhs.vic.edu.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "439631177",
  _teacher: new TeacherModel({
    school: schools[9]
  })
}), new UserModel({
  firstName: "Janet",
  lastName: "Elovaris",
  email: "elovaris.janet.j@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "Sarah Perry- 0466090784",
  _teacher: new TeacherModel({
    school: schools[10]
  })
}), new UserModel({
  firstName: "Chloe",
  lastName: "Le Merle",
  email: "chloelemerle@albertparkcollege.vic.edu.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "475414701",
  _teacher: new TeacherModel({
    school: schools[11]
  })
}), new UserModel({
  firstName: "Tanya",
  lastName: "Lane",
  email: "lane.tanya.t@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "416864965",
  _teacher: new TeacherModel({
    school: schools[12]
  })
}), new UserModel({
  firstName: "Tim",
  lastName: "Duivenvoorden",
  email: "tfd@mentonegrammar.net",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "431747640",
  _teacher: new TeacherModel({
    school: schools[13]
  })
}), new UserModel({
  firstName: "Gavin",
  lastName: "Clifford",
  email: "clifford.gavin.c@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "93971899",
  _teacher: new TeacherModel({
    school: schools[14]
  })
}), new UserModel({
  firstName: "Kath",
  lastName: "Harper",
  email: "charper.katharine.v@edumail.vic.gov.au",
  passwordHash: "",
  address: "",
  userType: UserType.TEACHER,
  phoneNumber: "432801338",
  _teacher: new TeacherModel({
    school: schools[15]
  })
}), new UserModel({
  firstName: "Phil",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.WED,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.FRI,
    }]
  })
}), new UserModel({
  firstName: "Pete",
  lastName: "B",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.TUE,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.THU,
    }]
  })
}), new UserModel({
  firstName: "Cheryl",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.WED,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }]
  })
}), new UserModel({
  firstName: "Debi",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.WED,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.THU,
    }]
  })
}), new UserModel({
  firstName: "Shane",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: false,
      afternoon: true,
      dayOfWeek: dayOfWeek.MON,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.FRI,
    }]
  })
}), new UserModel({
  firstName: "Fiona",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "32 Huffington Ave",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: true,
    reliable: true,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }]
  })
}), new UserModel({
  firstName: "Loriner",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.TUE,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.FRI,
    }]
  })
}), new UserModel({
  firstName: "Peter",
  lastName: "J",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: cities[0],
    trained: null,
    reliable: null,
    availabilities: [{
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.THU,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.MON,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.WED,
    }]
  })
}), new UserModel({
  firstName: "Catherine",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.WED,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }]
  })
}), new UserModel({
  firstName: "Danilo",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: null,
    availabilities: [{
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.FRI,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.TUE,
    }]
  })
}), new UserModel({
  firstName: "Mel",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.FRI,
    }, {
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.TUE,
    }]
  })
}), new UserModel({
  firstName: "Alan",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.THU,
    }, {
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.WED,
    }]
  })
}), new UserModel({
  firstName: "Tegan",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: null,
    availabilities: [{
      morning: true,
      afternoon: true,
      dayOfWeek: dayOfWeek.MON,
    }]
  })
}), new UserModel({
  firstName: "Karina",
  lastName: "",
  email: "",
  passwordHash: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: cities[0],
    trained: null,
    availabilities: [{
      morning: true,
      afternoon: false,
      dayOfWeek: dayOfWeek.THU,
    }]
  })
})];
const locations = [new LocationModel({
  name: "DWH",
  address: " ",
  capacity: 30,
  disabilityAccess: true,
  facilities: "projector",
}), new LocationModel({
  name: "Melb Uni",
  address: "",
  capacity: 30,
  disabilityAccess: true,
  facilities: "lecturn",
}), new LocationModel({
  name: "CAE417",
  address: "",
  capacity: 30,
  disabilityAccess: true,
  facilities: null,
})];
const workshops = [new WorkshopModel({
  workshopName: "Discussions of Homelessness and Disadvantage",
  requireFacilitator: true,
  requireGuestSpeaker: false,
}), new WorkshopModel({
  workshopName: "Finding belonging",
  requireFacilitator: true,
  requireGuestSpeaker: false,
}), new WorkshopModel({
  workshopName: "Big Idea Lecture",
  requireFacilitator: true,
  requireGuestSpeaker: false,
})];
const bookings = [new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 6, 11, 0), timeEnd: new Date(2018, 8, 6, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[0],
  firstTime: true,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 6, 15, 0), timeEnd: new Date(2018, 8, 6, 16, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[1],
  level: "5/6",
  teacher: users[1],
  firstTime: true,
  numberOfStudents: 27
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 7, 10, 0), timeEnd: new Date(2018, 8, 7, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[2],
  firstTime: true,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 7, 11, 0), timeEnd: new Date(2018, 8, 7, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[0],
  firstTime: false,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 7, 12, 0), timeEnd: new Date(2018, 8, 7, 13, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[3],
  firstTime: true,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 7, 13, 0), timeEnd: new Date(2018, 8, 7, 14, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[0],
  firstTime: false,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 8, 10, 0), timeEnd: new Date(2018, 8, 8, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "10",
  teacher: users[4],
  firstTime: true,
  numberOfStudents: 17
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 8, 10, 0), timeEnd: new Date(2018, 8, 8, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "8",
  teacher: users[5],
  firstTime: true,
  numberOfStudents: null
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 8, 11, 0), timeEnd: new Date(2018, 8, 8, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "8",
  teacher: users[5],
  firstTime: false,
  numberOfStudents: null
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 8, 11, 0), timeEnd: new Date(2018, 8, 8, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[0],
  firstTime: false,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 8, 12, 0), timeEnd: new Date(2018, 8, 8, 13, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[1],
  level: "1-6",
  teacher: users[6],
  firstTime: true,
  numberOfStudents: 13
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 10, 0), timeEnd: new Date(2018, 8, 9, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[2],
  level: null,
  teacher: users[7],
  firstTime: true,
  numberOfStudents: null
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 10, 0), timeEnd: new Date(2018, 8, 9, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[2],
  firstTime: false,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 10, 0), timeEnd: new Date(2018, 8, 9, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[8],
  firstTime: true,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 10, 0), timeEnd: new Date(2018, 8, 9, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[8],
  firstTime: false,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 11, 0), timeEnd: new Date(2018, 8, 9, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "7",
  teacher: users[9],
  firstTime: true,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 11, 0), timeEnd: new Date(2018, 8, 9, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[10],
  firstTime: true,
  numberOfStudents: 14
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 12, 0), timeEnd: new Date(2018, 8, 9, 13, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[11],
  firstTime: true,
  numberOfStudents: 30
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 12, 0), timeEnd: new Date(2018, 8, 9, 13, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[12],
  firstTime: true,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 12, 30), timeEnd: new Date(2018, 8, 9, 13, 30) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "7",
  teacher: users[9],
  firstTime: false,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 13, 0), timeEnd: new Date(2018, 8, 9, 14, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[8],
  firstTime: false,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 9, 13, 0), timeEnd: new Date(2018, 8, 9, 14, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[8],
  firstTime: false,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 10, 9, 0), timeEnd: new Date(2018, 8, 10, 10, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[13],
  firstTime: true,
  numberOfStudents: 18
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 10, 10, 0), timeEnd: new Date(2018, 8, 10, 11, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[14],
  firstTime: true,
  numberOfStudents: 25
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 10, 11, 0), timeEnd: new Date(2018, 8, 10, 12, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "7",
  teacher: users[9],
  firstTime: false,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 10, 12, 0), timeEnd: new Date(2018, 8, 10, 13, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "7",
  teacher: users[9],
  firstTime: false,
  numberOfStudents: 20
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 10, 13, 0), timeEnd: new Date(2018, 8, 10, 14, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "VCAL",
  teacher: users[15],
  firstTime: true,
  numberOfStudents: 15
}), new BookingModel({
  state: BookingState.PENDING,
  possibleTimes: [{ timeBegin: new Date(2018, 8, 10, 13, 0), timeEnd: new Date(2018, 8, 10, 14, 0) }],
  city: cities[0],
  location: null,
  workshop: workshops[0],
  level: "9",
  teacher: users[14],
  firstTime: false,
  numberOfStudents: 25
})];

export default {
  cities: cities,
  bookings: bookings,
  users: users,
  locations: locations,
  schools: schools,
  workshops: workshops
};

// export default {
//   "teacherPreference": teacherPreferences,
//   "cities": cities,
//   "users": users,
//   "teacherPreference": [{
//     "timeBegin": new Date(2019, 1, 20, 9, 30),
//     "timeEnd": new Date(2019, 1, 20, 10, 30),
//     "city": new CityModel({ city: "Melbourne" }),
//     "workshop": new WorkshopModel({
//       workshopName: "Homeless",
//       requireFacilitator: true, requireGuestSpeaker: true
//     }),
//     "level": "11",
//     "school": new SchoolModel({
//       name: "King's College",
//       city: new CityModel({ city: "Melbourne" })
//     }),
//     "contact": new UserModel({
//       firstName: "Steve",
//       lastName: "Wetherell",
//       email: "s.wether@gmail.com",
//       passwordHash: "021232",
//       address: "21 stevens ave, 1051",
//       userType: UserType.TEACHER,
//       phoneNumber: "0467399342",
//       _teacher: new TeacherModel({

//       })
//     }),
//     "return": false,
//     "numberOfStudents": 20,
//     "disabilityAccess": false
//   }],
//   "bookings": [{
//     "confirmed": true,
//     "faci": "Joe Blogs",
//     "guest speaker": "Richard Newton",
//     "due": 350,
//     "location": "Collins St",
//     "day": "Thursday",
//     "date": "2019/08/14",
//     "timeBeginHour": 9,
//     "timeBeginMinute": 0,
//     "timeEnd": "0100",
//     "area": "Melbourne",
//     "workshop": "Discussions about homelesness and disadvantage",
//     "level": 11,
//     "school": "King's College",
//     "contactName": "Steve Wetherell",
//     "return": false,
//     "numberOfStudents": 20,
//     "phoneNumber": "0467399328",
//     "disabilityAccess": false,
//     "contactEmail": "s.wetherell@gmail.com"
//   }],
//   "users": [{
//     "area": "Melbourne",
//     "contactName": "Jonny Bravo",
//     "phoneNumber": "045693952",
//     "contactEmail": "t.gould@gmail.com",
//     "availability": "2019-08-14T20:30:00.000",
//     "trained": true
//   }, {
//     "area": "Melbourne",
//     "contactName": "Jack Lee",
//     "phoneNumber": "045693953",
//     "contactEmail": "j.lee@gmail.com",
//     "availability": "2019-08-14T13:30:00.000",
//     "trained": false
//   }, {
//     "area": "Melbourne",
//     "contactName": "Mark Jackson",
//     "phoneNumber": "045693954",
//     "contactEmail": "m.jackson@gmail.com",
//     "availability": "2015-08-14T14:30:00.000",
//     "trained": true

//   }],
//   "guest speakers": [{
//     "area": "Melbourne",
//     "contactName": "Richard Newton",
//     "phoneNumber": "045633952",
//     "contactEmail": "r.newt@gmail.com",
//     "must work with": ["Tim Gould"],
//     "max per week": 4,
//     "availability": [],
//     "trained": false,
//     "reliable": false
//   }, {
//     "area": "Melbourne",
//     "contactName": "Steve Stevens",
//     "phoneNumber": "045633953",
//     "contactEmail": "s.stevens@gmail.com",
//     "must work with": [],
//     "max per week": 3,
//     "availability": [],
//     "trained": true,
//     "reliable": true
//   }, {
//     "area": "Melbourne",
//     "contactName": "Miles Ford",
//     "phoneNumber": "045633954",
//     "contactEmail": "m.ford@gmail.com",
//     "new": true,
//     "must work with": [],
//     "max per week": 2,
//     "availability": [],
//     "trained": true,
//     "reliable": false
//   }],
//   "locations": [{
//     "location name": "Lyle Theatre",
//     "address": "230 Collins St",
//     "capacity": 50,
//     "disability access": true,
//     "facilities": ["PC", "Scanner"]
//   }],
//   "workshops": [new WorkshopModel({
//     workshopName: "Homeless",
//     requireFacilitator: true, requireGuestSpeaker: true
//   })]
// };

// // city: new CityModel({ city: "Melbourne" }),
// // trained: true,
// // availabilities:
// //   [{
// //     morning: true,
// //     afternoon: true,
// //     dayOfWeek: dayOfWeek.MON
// //   }]
