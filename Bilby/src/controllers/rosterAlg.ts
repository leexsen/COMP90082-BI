import {
  eligible,
  userAvailable,
  pairTeams,
  adjustAvailabilities,
  checkBackToBackFacilitator,
  checkBackToBackGuestSpeaker,
  filterTeams,
  filterLocation,
  EMPTY_GUEST_SPEAKER,
  EMPTY_FACILITATOR,
} from "./userOperations";
import { Booking, BookingState } from "../models/booking.model";
import { User } from "../models/user.model";

/**
  * Base function for rostering preferences to facilitators
  * and guest speakers
  * @param {Booking[]} bookings - Array of bookings to roster.
  * @param {User[]} guestSpeakers - Array of working guest speakers.
  * @param {User[]} facilitators - Array of working facilitators.
  * @return {Booking[]} bookings - Array of rostered bookings.
  */
export default function rosterByPreferences(bookings: Booking[], guestSpeakers: User[], facilitators: User[]): Booking[] {
  for (let i = 0; i < bookings.length; i++) {
    let availableFacilitators: User[] = [];
    let availableGuestSpeakers: User[] = [];
    let teams: any = [];
    let backToBackFacilitator = false;
    let backToBackGuestSpeaker = false;

    const timeBegin = bookings[i].sessionTime.timeBegin;
    const timeEnd = bookings[i].sessionTime.timeEnd;
    const workshop = bookings[i].workshop;

    if (i > 0) {
      backToBackFacilitator = checkBackToBackFacilitator(bookings[i - 1], bookings[i]);
      backToBackGuestSpeaker = checkBackToBackGuestSpeaker(bookings[i - 1], bookings[i]);
    }

    if (!backToBackFacilitator && !backToBackGuestSpeaker) { // Neither facilitator nor guest speaker can do back to back.
      // From the user pool we select facilitators and guest speakers and check their availability for a specific booking
      availableFacilitators = facilitators.filter(user => userAvailable(user, timeBegin, timeEnd));
      availableGuestSpeakers = guestSpeakers.filter(user => userAvailable(user, timeBegin, timeEnd));

      if (i > 0) {
        availableFacilitators = availableFacilitators.filter(user => (user !== bookings[i - 1].facilitator));
        availableGuestSpeakers = availableGuestSpeakers.filter(user => (user !== bookings[i - 1].guestSpeaker));
      }

      // Crosscheck the workshop's constraints with user's attributes
      availableFacilitators = availableFacilitators.filter(user => eligible(user, workshop));
      availableGuestSpeakers = availableGuestSpeakers.filter(user => eligible(user, workshop));

      if (availableFacilitators.length === 0) {
        availableFacilitators.push(EMPTY_FACILITATOR);
      } if (availableGuestSpeakers.length === 0) {
        availableGuestSpeakers.push(EMPTY_GUEST_SPEAKER);
      }

      // Pair facilitators and guest speakers to follow the constraints
      for (let f = 0; f < availableFacilitators.length; f++) {
        for (let g = 0; g < availableGuestSpeakers.length; g++) {
          const pair = pairTeams(availableFacilitators[f], availableGuestSpeakers[g], workshop);

          if (pair !== null) {
            teams.push(pair);
          }
        }
      }
    } else if (!backToBackFacilitator && backToBackGuestSpeaker) { // Facilitator from previous booking can't do back to back but guest speaker can.
      availableFacilitators = facilitators.filter(user => userAvailable(user, timeBegin, timeEnd));
      availableFacilitators = availableFacilitators.filter(user => (user !== bookings[i - 1].facilitator));
      availableFacilitators = availableFacilitators.filter(user => eligible(user, workshop));

      const guestSpeaker = guestSpeakers.filter(user => user === bookings[i - 1].guestSpeaker)[0];

      if (availableFacilitators.length === 0) {
        availableFacilitators.push(EMPTY_FACILITATOR);
      }

      for (let f = 0; f < availableFacilitators.length; f++) {
        const pair = pairTeams(availableFacilitators[f], guestSpeaker, workshop);

        if (pair !== null) {
          teams.push(pair);
        }
      }
    } else if (backToBackFacilitator && !backToBackGuestSpeaker) { // Facilitator from previous booking can do back to back but guest speaker can't.
      availableGuestSpeakers = guestSpeakers.filter(user => userAvailable(user, timeBegin, timeEnd));
      availableGuestSpeakers = availableGuestSpeakers.filter(user => (user !== bookings[i - 1].guestSpeaker));
      availableGuestSpeakers = availableGuestSpeakers.filter(user => eligible(user, workshop));

      const facilitator = facilitators.filter(user => user === bookings[i - 1].facilitator)[0];

      if (availableGuestSpeakers.length === 0) {
        availableGuestSpeakers.push(EMPTY_GUEST_SPEAKER);
      }

      for (let g = 0; g < availableGuestSpeakers.length; g++) {
        const pair = pairTeams(facilitator, availableGuestSpeakers[g], workshop);

        if (pair !== null) {
          teams.push(pair);
        }
      }
    } else if (backToBackFacilitator && backToBackGuestSpeaker) { // Both facilitator and guest speaker from previous booking can do back to back.
      bookings[i].facilitator = bookings[i - 1].facilitator;
      bookings[i].guestSpeaker = bookings[i - 1].guestSpeaker;
      bookings[i].state = BookingState.UNCONFIRMED;

      const facilitator = facilitators.filter(user => user === bookings[i].facilitator)[0];
      const guestSpeaker = guestSpeakers.filter(user => user === bookings[i].guestSpeaker)[0];

      adjustAvailabilities(facilitator, timeBegin, timeEnd);
      adjustAvailabilities(guestSpeaker, timeBegin, timeEnd);
    }
    // Filter the pairs of users as to remove all accuracies of empty users whenever possible.
    teams = filterTeams(teams);

    if (i > 0) {
      teams = filterLocation(teams, bookings[i].location, bookings);
    }

    if (!(backToBackFacilitator && backToBackGuestSpeaker) && teams.length > 0) {
      const index = Math.floor(Math.random() * teams.length);
      bookings[i].facilitator = teams[index][0];
      bookings[i].guestSpeaker = teams[index][1];
      bookings[i].state = BookingState.UNCONFIRMED;

      adjustAvailabilities(teams[index][0], timeBegin, timeEnd);
      adjustAvailabilities(teams[index][1], timeBegin, timeEnd);
    }
  }

  return bookings;
}
