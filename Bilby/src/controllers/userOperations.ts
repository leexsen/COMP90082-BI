import { Facilitator, FacilitatorModel } from "../models/facilitator.model";
import { GuestSpeaker, GuestSpeakerModel } from "../models/guestSpeaker.model";
import { Workshop, WorkshopModel } from "../models/workshop.model";
import { User, UserModel, UserType } from "../models/user.model";
import { Ref } from "@hasezoey/typegoose";
import { Availability, Unavailability } from "../models/availability";
import { Booking } from "../models/booking.model";
import { Location, LocationModel } from "../models/location.model";
import { City, CityModel } from "../models/city.model";

export const EMPTY_FACILITATOR = new UserModel({
  firstName: "No Solution",
  lastName: "",
  email: "",
  address: "",
  userType: UserType.FACILITATOR,
  phoneNumber: "",
  _facilitator: new FacilitatorModel({
    city: new CityModel({ city: "" }),
    trained: [],
    reliable: false,
    availabilities: [],
    specificUnavailabilities: [],
    assignedTimes: []
  })
});

export const EMPTY_GUEST_SPEAKER = new UserModel({
  firstName: "No Solution",
  lastName: "",
  email: "",
  address: "",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "",
  _guestSpeaker: new GuestSpeakerModel({
    city: new CityModel({ city: "" }),
    trained: [],
    reliable: false,
    availabilities: [],
    specificUnavailabilities: [],
    assignedTimes: []
  })
});

export const NA_FACILITATOR = new UserModel({
  firstName: "N/A",
  lastName: "N/A",
  email: "N/A",
  address: "N/A",
  userType: UserType.FACILITATOR,
  phoneNumber: "N/A",
  _facilitator: new GuestSpeakerModel({
    city: new CityModel({ city: "N/A" }),
    trained: [],
    reliable: false,
    availabilities: [],
    specificUnavailabilities: [],
    assignedTimes: []
  })
});

export const NA_GUESTSPEAKER = new UserModel({
  firstName: "N/A",
  lastName: "N/A",
  email: "N/A",
  address: "N/A",
  userType: UserType.GUEST_SPEAKER,
  phoneNumber: "N/A",
  _guestSpeaker: new GuestSpeakerModel({
    city: new CityModel({ city: "N/A" }),
    trained: [],
    reliable: false,
    availabilities: [],
    specificUnavailabilities: [],
    assignedTimes: []
  })
});

/**
 * Check if dates are the same.
 *
 * @export
 * @param {Date} time1 - first date
 * @param {Date} time2 - second date
 * @returns {boolean} - whether the dates are the same
 */
export function checkSameTime(time1: Date, time2: Date): boolean {
  if (time1.getTime() === time2.getTime()) {
    return true;
  }
  return false;
}

/**
 * Check if day matches with availability.
 *
 * @export
 * @param {User} user - user to be assessed
 * @param {string} workshopName - name of workshop
 * @returns {boolean} - whether the user is trained for this particular workshop
 */
export function trainedUser(user: User, workshopName: string): boolean {
  if (user._facilitator instanceof FacilitatorModel) {
    const facilitator = user._facilitator as Facilitator;
    const trainedWorkshops = facilitator.trained;
    for (let i = 0; i < trainedWorkshops.length; i++) {
      if (trainedWorkshops[i] === workshopName) {
        return true;
      }
    }
  } else if (user._guestSpeaker instanceof GuestSpeakerModel) {
    const guestSpeaker = user._guestSpeaker as GuestSpeaker;
    const trainedWorkshops = guestSpeaker.trained;
    for (let j = 0; j < trainedWorkshops.length; j++) {
      if (trainedWorkshops[j] === workshopName) {
        return true;
      }
    }
  }

  return false;
}

/**
 * Check if user (facilitator or guest speaker) are eligible for a particular
 * workshop.
 *
 * @export
 * @param {User} user - user to check eligibility for
 * @param {Ref<Workshop>} workshop - workshop to check user against
 * @returns {boolean} - is the user available for the workshop
 */
export function eligible(user: User, workshop: Ref<Workshop>): boolean {
  if (workshop instanceof WorkshopModel) {
    const workshop1 = workshop as Workshop;
    if (trainedUser(user, workshop1.workshopName)) {
      return true;
    }
  }

  return false;
}

/**
 * Check if user is available for specified time.
 *
 * @export
 * @param {User} user - user to check
 * @param {Date} timeBegin - start of time block
 * @param {Date} timeEnd - end of time block
 * @returns {boolean} - whether the user is available
 */
export function userAvailable(user: User, timeBegin: Date, timeEnd: Date): boolean {
  let availabilities: Availability[] = [];
  let unavailabilities: Unavailability[] = [];
  let assignedTimes: Availability[] = [];

  if (user._facilitator instanceof FacilitatorModel) {
    const facilitator = user._facilitator as Facilitator;
    availabilities = facilitator.availabilities;
    unavailabilities = facilitator.specificUnavailabilities;
    assignedTimes = facilitator.assignedTimes;
  } else if (user._guestSpeaker instanceof GuestSpeakerModel) {
    const guestSpeaker = user._guestSpeaker as GuestSpeaker;
    availabilities = guestSpeaker.availabilities;
    unavailabilities = guestSpeaker.specificUnavailabilities;
    assignedTimes = guestSpeaker.assignedTimes;
  }

  let available = false;

  for (let i = 0; i < availabilities.length; i++) {
    if (availabilities[i].availableFrom <= timeBegin && availabilities[i].availableUntil >= timeEnd) {
      available = true;
    }
  }

  for (let i = 0; i < unavailabilities.length; i++) {
    if (unavailabilities[i].notAvailableFrom <= timeBegin && unavailabilities[i].notAvailableUntil >= timeEnd) {
      available = false;
    }
  }

  for (let i = 0; i < assignedTimes.length; i++) {
    if ((assignedTimes[i].availableFrom <= timeBegin && assignedTimes[i].availableUntil > timeBegin)
      || (assignedTimes[i].availableFrom < timeEnd && assignedTimes[i].availableUntil >= timeEnd)) {
      available = false;
    }
  }

  return available;
}

/**
 * Check how many back to back workshops has the user done.
 *
 * @export
 * @param {Availability[]} assignedTimes - array of times to which the user has been assigned
 * @param {Date} timeBegin - time for the booking we are currently trying to assign
 * @returns {number} - count of back to back workshops this user has done before current booking time
 */
export function checkBackToBackTime(assignedTimes: Availability[], timeBegin: Date): number {
  const formatedTimeBegin = new Date(timeBegin);
  let counter = 1;

  if (assignedTimes.length > 1) {
    for (let i = 0; i < (assignedTimes.length - 1); i++) {
      if (checkSameTime(assignedTimes[i].availableUntil, assignedTimes[i + 1].availableFrom) && assignedTimes[i + 1].availableUntil <= formatedTimeBegin) {
        counter++;
      }
    }
  } else if (assignedTimes.length === 0) {
    counter = 0;
  }

  return counter;
}

/**
 * Check if a facilitator can be rostered to a back to back booking.
 *
 * @export
 * @param {Booking} previousBooking - information from previously assigned booking
 * @param {Booking} currentBooking - current booking in need of assignment
 * @returns {boolean} - returns whether the facilitator from last booking can do this new booking or not
 */
export function checkBackToBackFacilitator(previousBooking: Booking, currentBooking: Booking): boolean {
  let sameCity = false;
  let sameLocation = false;
  let eligibleForWorkshop = false;
  let maxAmount = false;
  let available = false;

  if (previousBooking.city instanceof CityModel && currentBooking.city instanceof CityModel) {
    const previousCity = previousBooking.city as City;
    const currentCity = currentBooking.city as City;
    if (previousCity.city === currentCity.city) {
      sameCity = true;
    }
  }

  if (previousBooking.location instanceof LocationModel && currentBooking.location instanceof LocationModel) {
    const previousLocation = previousBooking.location as Location;
    const currentLocation = currentBooking.location as Location;
    if (previousLocation.name === currentLocation.name) {
      sameLocation = true;
    }
  }

  if (previousBooking.facilitator instanceof UserModel) {
    const facilitator = previousBooking.facilitator as User;
    if (eligible(facilitator, currentBooking.workshop)) {
      eligibleForWorkshop = true;
    }

    if (facilitator._facilitator instanceof FacilitatorModel) {
      const _facilitator = facilitator._facilitator as Facilitator;
      const counter = checkBackToBackTime(_facilitator.assignedTimes, currentBooking.sessionTime.timeBegin);
      if (counter >= 3) {
        maxAmount = true;
      }
    }
  }

  if (previousBooking.facilitator instanceof UserModel) {
    available = userAvailable(previousBooking.facilitator as User, currentBooking.sessionTime.timeBegin, currentBooking.sessionTime.timeEnd);
  }

  if (sameCity && sameLocation && eligibleForWorkshop && !maxAmount && available) {
    return true;
  }
  return false;
}

/**
 * Check if a guest speaker can be rostered to a back to back booking.
 *
 * @export
 * @param {Booking} previousBooking - information from previously assigned booking
 * @param {Booking} currentBooking - current booking in need of assignment
 * @returns {boolean} - returns whether the guest speaker from last booking can do this new booking or not
 */
export function checkBackToBackGuestSpeaker(previousBooking: Booking, currentBooking: Booking): boolean {
  let sameCity = false;
  let sameLocation = false;
  let eligibleForWorkshop = false;
  let maxAmount = false;
  let available = false;

  if (previousBooking.city instanceof CityModel && currentBooking.city instanceof CityModel) {
    const previousCity = previousBooking.city as City;
    const currentCity = currentBooking.city as City;
    if (previousCity.city === currentCity.city) {
      sameCity = true;
    }
  }

  if (previousBooking.location instanceof LocationModel && currentBooking.location instanceof LocationModel) {
    const previousLocation = previousBooking.location as Location;
    const currentLocation = currentBooking.location as Location;
    if (previousLocation.name === currentLocation.name) {
      sameLocation = true;
    }
  }

  if (previousBooking.guestSpeaker instanceof UserModel) {
    const guestSpeaker = previousBooking.guestSpeaker as User;
    if (eligible(guestSpeaker, currentBooking.workshop)) {
      eligibleForWorkshop = true;
    }

    if (guestSpeaker._guestSpeaker instanceof GuestSpeakerModel) {
      const _guestSpeaker = guestSpeaker._guestSpeaker as GuestSpeaker;
      const counter = checkBackToBackTime(_guestSpeaker.assignedTimes, currentBooking.sessionTime.timeBegin);
      if (counter >= 2) {
        maxAmount = true;
      }
    }
  }

  if (previousBooking.guestSpeaker instanceof UserModel) {
    available = userAvailable(previousBooking.guestSpeaker as User, currentBooking.sessionTime.timeBegin, currentBooking.sessionTime.timeEnd);
  }

  if (sameCity && sameLocation && eligibleForWorkshop && !maxAmount && available) {
    return true;
  }
  return false;
}

/**
 * Adjust availabilities when user is rostered for a booking.
 *
 * @param {User} user - user to whom we want to change availabilities
 * @param {Date} timeBegin - time from which we want to make user unavailable
 * @param {Date} timeEnd - time until which we want to make user unavailable
 * @returns {void} void
 */
export function adjustAvailabilities(user: User, timeBegin: Date, timeEnd: Date): void {
  if (user._facilitator instanceof FacilitatorModel) {
    const facilitator = user._facilitator as Facilitator;
    facilitator.assignedTimes.push({ availableFrom: timeBegin, availableUntil: timeEnd });
  } else if (user._guestSpeaker instanceof GuestSpeakerModel) {
    const guestSpeaker = user._guestSpeaker as GuestSpeaker;
    guestSpeaker.assignedTimes.push({ availableFrom: timeBegin, availableUntil: timeEnd });
  }
}

/**
 * Check if facilitator and guest speaker can work with each other and pair them
 * for booking.
 *
 * @export
 * @param {User} possibleFacilitator - facilitator to check
 * @param {User} possibleGuestSpeaker - guestSpeaker to check
 * @param {Ref<Workshop>} workshop - workshop of current booking
 * @returns {(null | [User, User])} - null if the users can't be paired, array
 *                                    containing users if the can be
 */
export function pairTeams(possibleFacilitator: User, possibleGuestSpeaker: User, workshop: Ref<Workshop>): null | [User, User] {
  let team: [User, User];

  if (possibleFacilitator._facilitator instanceof FacilitatorModel && possibleGuestSpeaker._guestSpeaker instanceof GuestSpeakerModel && workshop instanceof WorkshopModel) {
    const workshop1 = workshop as Workshop;
    const workshopName = workshop1.workshopName;

    if (workshop1.requireFacilitator && workshop1.requireGuestSpeaker) {
      if (possibleFacilitator !== EMPTY_FACILITATOR && possibleGuestSpeaker !== EMPTY_GUEST_SPEAKER) {
        if (trainedUser(possibleFacilitator, workshopName) && trainedUser(possibleGuestSpeaker, workshopName)) {
          team = [possibleFacilitator, possibleGuestSpeaker];
          return team;
        } else if (trainedUser(possibleFacilitator, workshopName) && !trainedUser(possibleGuestSpeaker, workshopName)) {
          team = [possibleFacilitator, EMPTY_GUEST_SPEAKER];
          return team;
        } else if (!trainedUser(possibleFacilitator, workshopName) && trainedUser(possibleGuestSpeaker, workshopName)) {
          team = [EMPTY_FACILITATOR, possibleGuestSpeaker];
          return team;
        } else if (!trainedUser(possibleFacilitator, workshopName) && !trainedUser(possibleGuestSpeaker, workshopName)) {
          team = [EMPTY_FACILITATOR, EMPTY_GUEST_SPEAKER];
          return team;
        }
      } else if (possibleFacilitator !== EMPTY_FACILITATOR) {
        if (trainedUser(possibleFacilitator, workshopName)) {
          team = [possibleFacilitator, possibleGuestSpeaker];
          return team;
        } else if (!trainedUser(possibleFacilitator, workshopName)) {
          team = [EMPTY_FACILITATOR, possibleGuestSpeaker];
          return team;
        }
      } else if (possibleGuestSpeaker !== EMPTY_GUEST_SPEAKER) {
        if (trainedUser(possibleGuestSpeaker, workshopName)) {
          team = [possibleFacilitator, possibleGuestSpeaker];
          return team;
        } else if (!trainedUser(possibleGuestSpeaker, workshopName)) {
          team = [possibleFacilitator, EMPTY_GUEST_SPEAKER];
          return team;
        }
      } else {
        team = [possibleFacilitator, possibleGuestSpeaker];
        return team;
      }
    } else if (workshop1.requireFacilitator && !workshop1.requireGuestSpeaker) {
      if (!(possibleFacilitator === EMPTY_FACILITATOR) && trainedUser(possibleFacilitator, workshopName)) {
        team = [possibleFacilitator, NA_GUESTSPEAKER];
        return team;
      } else {
        team = [EMPTY_FACILITATOR, NA_GUESTSPEAKER];
        return team;
      }
    } else if (!workshop1.requireFacilitator && workshop1.requireGuestSpeaker) {
      if (possibleGuestSpeaker !== EMPTY_GUEST_SPEAKER && trainedUser(possibleGuestSpeaker, workshopName)) {
        team = [NA_FACILITATOR, possibleGuestSpeaker];
        return team;
      } else {
        team = [NA_FACILITATOR, EMPTY_GUEST_SPEAKER];
        return team;
      }
    }
  }

  return null;
}

/**
 * Check if facilitator and guest speaker can work with each other and pair them
 * for booking.
 *
 * @export
 * @param {[User, User][]} teams - array of possible teams
 * @returns {[User, User][]} - array of most suitable teams
 */
export function filterTeams(teams: [User, User][]): [User, User][] {
  const noEmptyUsers = teams.filter(team => team[0] !== EMPTY_FACILITATOR && team[1] !== EMPTY_GUEST_SPEAKER);
  let noEmptyFacilitators: [User, User][];
  let noEmptyGuestSpeakers: [User, User][];

  if (noEmptyUsers.length > 0) {
    return noEmptyUsers;
  } else if (noEmptyUsers.length === 0) {
    noEmptyFacilitators = teams.filter(team => team[0] !== EMPTY_FACILITATOR);
    noEmptyGuestSpeakers = teams.filter(team => team[1] !== EMPTY_GUEST_SPEAKER);

    if (noEmptyFacilitators.length > 0) {
      return noEmptyFacilitators;
    } else if (noEmptyGuestSpeakers.length > 0) {
      return noEmptyGuestSpeakers;
    }
  }

  return teams;
}

/**
 * Check if facilitator and guest speaker can work with each other and pair them
 * for booking.
 *
 * @export
 * @param {[User, User][]} teams - array of possible teams
 * @param {Ref<Location>} currentLocation - location of the current booking
 * @param {Booking[]} bookings - array of all bookings
 * @returns {[User, User][]} - array of most suitable teams
 */
export function filterLocation(teams: [User, User][], currentLocation: Ref<Location>, bookings: Booking[]): [User, User][] {
  let newTeams: [User, User][] = [];
  let goodPair = false;
  let goodIndividual = false;

  for (let i = 0; i < teams.length; i++) {
    const rosteredFacilitatorBookings = bookings.filter(booking => booking.facilitator === teams[i][0]);
    const rosteredGuestSpeakerBookings = bookings.filter(booking => booking.guestSpeaker === teams[i][1]);

    if (rosteredFacilitatorBookings.length !== 0 && rosteredGuestSpeakerBookings.length !== 0) {
      if (rosteredFacilitatorBookings[rosteredFacilitatorBookings.length - 1].location === currentLocation
        && rosteredGuestSpeakerBookings[rosteredGuestSpeakerBookings.length - 1].location === currentLocation) {
        if (!goodPair) {
          newTeams = [];
          goodPair = true;
        }
        newTeams.push(teams[i]);
      }
    } else if (rosteredGuestSpeakerBookings.length !== 0 && !goodPair) {
      if (rosteredGuestSpeakerBookings[rosteredGuestSpeakerBookings.length - 1].location === currentLocation) {
        if (!goodIndividual) {
          newTeams = [];
          goodIndividual = true;
        }
        newTeams.push(teams[i]);
      }
    } else if (rosteredFacilitatorBookings.length !== 0 && !goodPair) {
      if (rosteredFacilitatorBookings[rosteredFacilitatorBookings.length - 1].location === currentLocation) {
        if (!goodIndividual) {
          newTeams = [];
          goodIndividual = true;
        }
        newTeams.push(teams[i]);
      }
    } else if (!(goodPair && goodIndividual)) {
      newTeams.push(teams[i]);
    }
  }

  if (newTeams.length > 0) {
    return newTeams;
  }

  return teams;
}
