//package com.example.SOCscheduler.services;
//
//import com.example.SOCscheduler.model.Schedule;
//import com.example.SOCscheduler.model.ScheduleType;
//import com.example.SOCscheduler.model.Shift;
//import com.example.SOCscheduler.model.Userr;
//import com.example.SOCscheduler.repositories.ScheduleRepository;
//import com.example.SOCscheduler.repositories.ShiftRepository;
//import com.example.SOCscheduler.repositories.UserrRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class SchedulingService {
//
//    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);
//
//    @Autowired
//    private UserrRepository userRepository;
//
//    @Autowired
//    private ShiftRepository shiftRepository;
//
//    @Autowired
//    private ScheduleRepository scheduleRepository;
//
//    public void generateSchedule(LocalDate startDate, int days) {
//        List<Shift> shifts = shiftRepository.findAll();
//        List<Userr> users = userRepository.findAll();
//        LocalDate endDate = startDate.plusDays(days - 1);
//
//        // Check for already scheduled dates
//        List<LocalDate> existingDates = scheduleRepository.findDistinctDatesBetween(startDate, endDate);
//        if (!existingDates.isEmpty()) {
//            throw new IllegalArgumentException("Schedules already exist for the following dates: " + existingDates);
//        }
//
//        // Prepare shifts for female users
//        List<Shift> femaleShifts = shifts.stream()
//                .filter(shift -> !"Shift 3".equals(shift.getName()))
//                .collect(Collectors.toList());
//
//        logger.info("Generating schedule from {} for {} days", startDate, days);
//
//        for (int i = 0; i < days; i++) {
//            LocalDate currentDate = startDate.plusDays(i);
//            boolean isSunday = currentDate.getDayOfWeek() == DayOfWeek.SUNDAY;
//
//            logger.info("Processing date: {}", currentDate);
//
//            // Shuffle users to ensure randomness in assignment
//            Collections.shuffle(users);
//
//            // Assign shifts for each day
//            for (Shift shift : shifts) {
//                logger.info("Processing shift: {}", shift.getName());
//
//                if (shift.getName().equals("Shift 3")) {
//                    assignShift3(users, shift, currentDate, isSunday);
//                } else {
//                    assignOtherShifts(users, shift, currentDate, isSunday, femaleShifts);
//                }
//            }
//
//            // Assign regular jobs to users not scheduled for SOC shifts on non-Sundays
//            if (!isSunday) {
//                assignRegularJobs(users, currentDate);
//            }
//        }
//
//        // Verify weekly working hours for all users
//        verifyWeeklyWorkingHours(users, startDate, endDate);
//    }
//
//    private void assignShift3(List<Userr> users, Shift shift, LocalDate date, boolean isSunday) {
//        List<Userr> availableUsers = users.stream()
//                .filter(user -> !"female".equalsIgnoreCase(user.getGender()))
//                .filter(user -> !isAlreadyScheduled(user, date))
//                .filter(user -> !hasAssignedShift(user, date, shift))
//                .collect(Collectors.toList());
//
//        logger.info("Assigning Shift 3 to users");
//
//        for (int i = 0; i < 2; i++) {
//            if (i < availableUsers.size()) {
//                Userr user = availableUsers.get(i);
//                scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT)); // Set SOC_SHIFT type
//
//                // Set the next day as day off
//                LocalDate dayOff = date.plusDays(1);
//                if (!isAlreadyScheduled(user, dayOff)) {
//                    scheduleRepository.save(new Schedule(user, null, dayOff, ScheduleType.DAY_OFF));
//                }
//            }
//        }
//    }
//
//
//    private void assignOtherShifts(List<Userr> users, Shift shift, LocalDate date, boolean isSunday, List<Shift> femaleShifts) {
//        List<Userr> availableUsers = users.stream()
//                .filter(user -> !isAlreadyScheduled(user, date))
//                .filter(user -> !("female".equalsIgnoreCase(user.getGender()) && "Shift 3".equals(shift.getName())))
//                .collect(Collectors.toList());
//
//        // Sort users to ensure fair distribution
//        availableUsers = sortUsersByAssignmentCount(availableUsers, shift);
//
//        logger.info("Assigning other shifts");
//
//        for (int i = 0; i < 2; i++) {
//            if (i < availableUsers.size()) {
//                Userr user = availableUsers.get(i);
//                if (shift.getName().equals("Shift 3") && isSunday) {
//                    continue;
//                }
//                // Set the schedule type to SOC_SHIFT for all SOC shifts (1, 2, and 3)
//                ScheduleType scheduleType = ScheduleType.SOC_SHIFT;
//                scheduleRepository.save(new Schedule(user, shift, date, scheduleType));
//            }
//        }
//    }
//
//
//
//
//
//    private void assignRegularJobs(List<Userr> users, LocalDate date) {
//        for (Userr user : users) {
//            boolean isScheduledForSOCShift = isAlreadyScheduled(user, date) && hasAssignedShift(user, date, null);
//            boolean hasDayOff = isScheduledForSOCShift && !scheduleRepository.existsByUserAndDateAndShift(user, date, null);
//
//            // Assign regular job only if the user is not scheduled for a SOC shift and doesn't have a day off
//            if (!isScheduledForSOCShift && !hasDayOff) {
//                Schedule regularJob = new Schedule(user, null, date, ScheduleType.REGULAR_JOB);
//                scheduleRepository.save(regularJob);
//            }
//        }
//    }
//
//
//    private boolean isAlreadyScheduled(Userr user, LocalDate date) {
//        return scheduleRepository.existsByUserAndDate(user, date);
//    }
//    private boolean hasDayOffScheduled(Userr user, LocalDate date) {
//        return scheduleRepository.existsByUserAndDateAndType(user, date, ScheduleType.DAY_OFF);
//    }
//
//
//    private boolean hasAssignedShift(Userr user, LocalDate date, Shift shift) {
//        return scheduleRepository.existsByUserAndDateAndShift(user, date, shift);
//    }
//
//    private List<Userr> sortUsersByAssignmentCount(List<Userr> users, Shift shift) {
//        return users.stream()
//                .sorted((u1, u2) -> Long.compare(
//                        scheduleRepository.countByUserAndShift(u1, shift),
//                        scheduleRepository.countByUserAndShift(u2, shift)
//                ))
//                .collect(Collectors.toList());
//    }
//
//    private void verifyWeeklyWorkingHours(List<Userr> users, LocalDate startDate, LocalDate endDate) {
//        for (Userr user : users) {
//            Long socHours = scheduleRepository.countSocHoursByUser(user, startDate, endDate);
//            Long regularHours = scheduleRepository.countRegularHoursByUser(user, startDate, endDate);
//            Long dayOffHours = scheduleRepository.countDayOffHoursByUser(user, startDate, endDate);
//
//            // Ensure 8 hours credit for each day-off
//            Long totalDayOffHours = (dayOffHours != null ? dayOffHours : 0) * 8;
//            Long totalHours = (socHours != null ? socHours : 0) + (regularHours != null ? regularHours : 0) + totalDayOffHours;
//
//            if (totalHours != 48) {
//                logger.warn("User {} has worked {} hours (SOC: {}, Regular: {}, Day-Off: {}) instead of 48 hours between {} and {}",
//                        user.getName(), totalHours, socHours != null ? socHours : 0, regularHours != null ? regularHours : 0, totalDayOffHours, startDate, endDate);
//            }
//        }
//    }
//
//
//    public List<Schedule> getAllSchedules() {
//        return scheduleRepository.findAll();
//    }
//
//    public Map<LocalDate, Map<String, List<String>>> getFormattedSchedules() {
//        List<Schedule> schedules = scheduleRepository.findAll();
//
//        return schedules.stream().collect(Collectors.groupingBy(
//                Schedule::getDate,
//                Collectors.groupingBy(
//                        schedule -> schedule.getShift() == null ? "Day-Off" : schedule.getShift().getName(),
//                        Collectors.mapping(schedule -> schedule.getUser() == null ? "Unknown User" : schedule.getUser().getName(), Collectors.toList())
//                )
//        ));
//    }
//}
package com.example.SOCscheduler.services;

import com.example.SOCscheduler.model.Schedule;
import com.example.SOCscheduler.model.ScheduleType;
import com.example.SOCscheduler.model.Shift;
import com.example.SOCscheduler.model.Userr;
import com.example.SOCscheduler.repositories.ScheduleRepository;
import com.example.SOCscheduler.repositories.ShiftRepository;
import com.example.SOCscheduler.repositories.UserrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    @Autowired
    private UserrRepository userRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public void generateSchedule(LocalDate startDate, int days, boolean isThreeShiftScenario) {
        List<Shift> shifts = shiftRepository.findAll();
        List<Userr> users = userRepository.findAll();
        LocalDate endDate = startDate.plusDays(days - 1);

        // Check for already scheduled dates
        List<LocalDate> existingDates = scheduleRepository.findDistinctDatesBetween(startDate, endDate);
        if (!existingDates.isEmpty()) {
            throw new IllegalArgumentException("Schedules already exist for the following dates: " + existingDates);
        }

        // Prepare shifts for female users based on the scenario
        List<Shift> femaleShifts = shifts.stream()
                .filter(shift -> {
                    if (isThreeShiftScenario && "Shift 3".equals(shift.getName())) {
                        return false;
                    }
                    if (!isThreeShiftScenario && "Shift 2".equals(shift.getName())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());


        logger.info("Generating schedule from {} for {} days", startDate, days);

        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            boolean isSunday = currentDate.getDayOfWeek() == DayOfWeek.SUNDAY;

            logger.info("Processing date: {}", currentDate);

            // Shuffle users to ensure randomness in assignment
            Collections.shuffle(users);

            List<Shift> relevantShifts = shifts.stream()
                    .filter(shift -> {
                        if (isThreeShiftScenario) {
                            return Shift.ShiftType.THREE_SHIFT.equals(shift.getShiftType()) &&
                                    ("Shift 1".equals(shift.getName()) ||
                                            "Shift 2".equals(shift.getName()) ||
                                            "Shift 3".equals(shift.getName()));
                        } else {
                            return Shift.ShiftType.TWO_SHIFT.equals(shift.getShiftType()) &&
                                    ("Shift 1".equals(shift.getName()) ||
                                            "Shift 2".equals(shift.getName()));
                        }
                    })
                    .collect(Collectors.toList());


            // Assign shifts for each day
            for (Shift shift : relevantShifts) {
                logger.info("Processing shift: {}", shift.getName());

                if (isThreeShiftScenario) {
                    if (shift.getName().equals("Shift 3")) {
                        assignShift3(users, shift, currentDate, isSunday);
                    } else {
                        assignOtherShifts(users, shift, currentDate, isSunday, femaleShifts, "THREE_SHIFT");
                    }
                } else {
                    if (shift.getName().equals("Shift 2")) {
                        assignShift2(users, shift, currentDate);
                    } else {
                        assignOtherShifts(users, shift, currentDate, isSunday, femaleShifts, "TWO_SHIFT");
                    }
                }
            }

            // Assign regular jobs to users not scheduled for SOC shifts on non-Sundays
            if (!isSunday) {
                assignRegularJobs(users, currentDate);
            }
        }

        // Verify weekly working hours for all users
        verifyWeeklyWorkingHours(users, startDate, endDate);
    }


    private void assignShift3(List<Userr> users, Shift shift, LocalDate date, boolean isSunday) {
        // If it's Sunday, filter out users assigned on the previous Sunday
        if (isSunday) {
            List<Userr> usersAssignedLastSunday = scheduleRepository.findUsersAssignedOnLastSunday(date.minusWeeks(1), ScheduleType.SOC_SHIFT);

            users = users.stream()
                    .filter(user -> !usersAssignedLastSunday.contains(user)) // Filter out users assigned last Sunday
                    .filter(user -> user.getId() == 1 || !"female".equalsIgnoreCase(user.getGender())) // Further filter by gender
                    .collect(Collectors.toList());

            // If no users are left after filtering, log the information and return
            if (users.isEmpty()) {
                logger.info("No available users to assign for Shift 3 on Sunday");
                return;
            }
        }

        // Filter out User ID 1 for certain days
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.TUESDAY ||
                dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.MONDAY) {
            users = users.stream()
                    .filter(user -> user.getId() != 1) // Exclude User ID 1 on these days
                    .collect(Collectors.toList());
        }

        // Specific scheduling for User ID 1 on Fridays
        if (isUserIdOne(users, 1, date)) {
            // User ID 1 specific schedule on Friday
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                Userr user = getUserById(users, 1);
                scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT));

                // Assign a day off for User ID 1 after Shift 3 on Friday
                LocalDate dayOff = date.plusDays(1); // The next day
                if (!isAlreadyScheduled(user, dayOff)) {
                    scheduleRepository.save(new Schedule(user, null, dayOff, ScheduleType.DAY_OFF));
                }
                return; // Exit as we have assigned the shift and day off
            }
        }

        // Filter available users
        List<Userr> availableUsers = users.stream()
                .filter(user -> !"female".equalsIgnoreCase(user.getGender())) // Filter by gender for Shift 3
                .filter(user -> !isAlreadyScheduled(user, date)) // Check if the user is already scheduled on the same day
                .filter(user -> !hasAssignedShift(user, date, shift)) // Check if the user has been assigned this shift
                .filter(user -> !isAssignedToShiftThisWeek(user, shift, date)) // Check if the user has this shift during the week
                .collect(Collectors.toList());

        logger.info("Assigning Shift 3 to users");

        // Ensure fair distribution and prevent excessive repetitions
        Map<Userr, Long> shiftCounts = getShiftCounts(availableUsers, shift);

        // Sort users by the number of shifts they have been assigned
        List<Userr> sortedUsers = sortUsersByShiftCount(availableUsers, shiftCounts);

        // Assign the shift to the first available user
        if (!availableUsers.isEmpty()) {
            Userr user = sortedUsers.get(0);  // Pick the first available user
            scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT));

            // Set the next day as a day off
            LocalDate dayOff = date.plusDays(1);
            if (!isAlreadyScheduled(user, dayOff)) {
                scheduleRepository.save(new Schedule(user, null, dayOff, ScheduleType.DAY_OFF));
            }
        }
    }


    private void assignShift2(List<Userr> users, Shift shift, LocalDate date) {
        List<Userr> availableUsers = users.stream()
                .filter(user -> !"female".equalsIgnoreCase(user.getGender()))
                .filter(user -> !isAlreadyScheduled(user, date))
                .filter(user -> !isAssignedToShiftThisWeek(user, shift, date))
                .collect(Collectors.toList());

        logger.info("Assigning Shift 2 to users");

        // Ensure fair distribution and prevent excessive repetitions
        Map<Userr, Long> shiftCounts = getShiftCounts(availableUsers, shift);

        // Sort users by the number of shifts they have been assigned
        List<Userr> sortedUsers = sortUsersByShiftCount(availableUsers, shiftCounts);

        // Assign Shift 2 to 2 users
//        for (int i = 0; i < 2; i++) {
//            if (i < sortedUsers.size()) {
//                Userr user = sortedUsers.get(i);
//                scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT));
//
//                // Set the next day as day off
//                LocalDate dayOff = date.plusDays(1);
//                if (!isAlreadyScheduled(user, dayOff)) {
//                    scheduleRepository.save(new Schedule(user, null, dayOff, ScheduleType.DAY_OFF));
//                }
//            }
//        }
        if (!sortedUsers.isEmpty()) {
            // Get the first user from the sorted list
            Userr user = sortedUsers.get(0);

            // Check if the user is already assigned a shift on the same day
            if (!isAlreadyScheduled(user, date)) {
                // Assign the user to the shift
                scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT));

                // Set the next day as a day off if not already scheduled
                LocalDate dayOff = date.plusDays(1);
                if (!isAlreadyScheduled(user, dayOff)) {
                    scheduleRepository.save(new Schedule(user, null, dayOff, ScheduleType.DAY_OFF));
                }
            }
        }

    }

    private void assignOtherShifts(List<Userr> users, Shift shift, LocalDate date, boolean isSunday, List<Shift> femaleShifts, String shiftType) {
        // If it's Sunday, ensure that users who were assigned last Sunday are excluded
        if (isSunday) {
            // Get users assigned last Sunday and filter them out
            List<Userr> usersAssignedLastSunday = scheduleRepository.findUsersAssignedOnLastSunday(date.minusWeeks(1), ScheduleType.SOC_SHIFT);

            users = users.stream()
                    .filter(user -> !usersAssignedLastSunday.contains(user)) // Filter out users assigned last Sunday
                    .collect(Collectors.toList());

            // If no users are left after filtering, log the information and return
            if (users.isEmpty()) {
                logger.info("No available users to assign for shift: " + shift.getName() + " on Sunday");
                return;
            }
        }




        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.FRIDAY ||
                dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.MONDAY) {
            users = users.stream()
                    .filter(user -> user.getId() != 1) // Filter out User ID 1 on these days
                    .collect(Collectors.toList());
        }

        // Filter users based on the shift type and gender rules
        List<Userr> availableUsers = users.stream()
                .filter(user -> !isAlreadyScheduled(user, date))
                .filter(user -> !isAssignedToShiftThisWeek(user, shift, date))
//                .filter(user -> getTotalHoursForWeek(user, date) + shift.getHours() <= 48)
//                .filter(user -> {
//                    // Allow females on Sundays, but enforce restrictions on weekdays
//                    if (isSunday) {
//                        return true; // Allow all users on Sunday
//                    }
//                    // For three-shift scenario
//                    if ("three_shift".equals(shiftType)) {
//                        return !("female".equalsIgnoreCase(user.getGender()) && "Shift 3".equals(shift.getName()));
//                    }
//                    // For two-shift scenario
//                    if ("two_shift".equals(shiftType)) {
//                        return !("female".equalsIgnoreCase(user.getGender()) && "Shift 2".equals(shift.getName()));
//                    }
//                    return true; // Default case
//                })

                .collect(Collectors.toList());

        // Sort users to ensure fair distribution
        Map<Userr, Long> shiftCounts = getShiftCounts(availableUsers, shift);
        List<Userr> sortedUsers = sortUsersByShiftCount(availableUsers, shiftCounts);

        logger.info("Assigning other shifts for shift type: " + shiftType);

        if (!availableUsers.isEmpty()) {
            Userr user = availableUsers.get(0);  // Pick the first available user
            scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT));
        }
    }

    private List<Userr> getAllUsers() {
        return userRepository.findAll(); // Fetches all users from the database
    }


    private void assignRegularJobs(List<Userr> users, LocalDate date) {
        for (Userr user : users) {

            // Check if the user is scheduled for any SOC shift on the given date
            boolean isScheduledForSOCShift = scheduleRepository.existsByUserAndDateAndType(user, date, ScheduleType.SOC_SHIFT);

            // Check if the user has a day off scheduled on the given date
            boolean hasDayOff = hasDayOffScheduled(user, date);

            // Assign a regular job only if the user is not scheduled for any SOC shift and does not have a day off
            if (!isScheduledForSOCShift && !hasDayOff) {
                Schedule regularJob = new Schedule(user, null, date, ScheduleType.REGULAR_JOB);
                scheduleRepository.save(regularJob);
            }
        }
    }

    private boolean isAlreadyScheduled(Userr user, LocalDate date) {
        return scheduleRepository.existsByUserAndDate(user, date);
    }

    private boolean hasDayOffScheduled(Userr user, LocalDate date) {
        return scheduleRepository.existsByUserAndDateAndType(user, date, ScheduleType.DAY_OFF);
    }

    private boolean hasAssignedShift(Userr user, LocalDate date, Shift shift) {
        return scheduleRepository.existsByUserAndDateAndShift(user, date, shift);
    }

    private Map<Userr, Long> getShiftCounts(List<Userr> users, Shift shift) {
        Map<Userr, Long> shiftCounts = new HashMap<>();
        for (Userr user : users) {
            Long count = scheduleRepository.countByUserAndShift(user, shift);
            shiftCounts.put(user, count);
        }
        return shiftCounts;
    }

    private List<Userr> sortUsersByShiftCount(List<Userr> users, Map<Userr, Long> shiftCounts) {
        return users.stream()
                .sorted((u1, u2) -> Long.compare(
                        shiftCounts.getOrDefault(u1, 0L),
                        shiftCounts.getOrDefault(u2, 0L)
                ))
                .collect(Collectors.toList());
    }
    private void assignShiftOnSunday(List<Userr> users, Shift shift, LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            // Get users who were assigned on the previous Sunday
            List<Userr> usersAssignedLastSunday = getUsersAssignedLastSunday(date);

            // Filter out users who were assigned on the previous Sunday
            users = users.stream()
                    .filter(user -> !usersAssignedLastSunday.contains(user))
                    .collect(Collectors.toList());

            // If all users have been rotated, reset the rotation and allow them to be reassigned
            if (users.isEmpty()) {
                users = getAllAvailableUsers(); // Reset the list with all users
            }
        }

        // Now assign shifts to the available users, ensuring fair distribution
        List<Userr> availableUsers = users.stream()
                .filter(user -> !isAlreadyScheduled(user, date))
                .filter(user -> getTotalHoursForWeek(user, date) + shift.getHours() <= 48)
                .collect(Collectors.toList());

        // Ensure fair distribution by tracking the number of Sunday assignments
        Map<Userr, Long> sundayShiftCounts = getShiftCounts(availableUsers, shift);

        // Sort users based on the number of Sunday shifts they've been assigned
        List<Userr> sortedUsers = sortUsersByShiftCount(availableUsers, sundayShiftCounts);

        if (!availableUsers.isEmpty()) {
            Userr user = availableUsers.get(0);  // Pick the first available user
            scheduleRepository.save(new Schedule(user, shift, date, ScheduleType.SOC_SHIFT));
        }
    }

    public List<Userr> getAllAvailableUsers() {
        // Retrieve all users from the database
        List<Userr> allUsers = userRepository.findAll();

        // Filter out users who are already assigned shifts for that day or week (if needed)
        List<Userr> availableUsers = allUsers.stream()
                .filter(user -> !isAlreadyScheduled(user, LocalDate.now())) // Adjust the filter condition as needed
                .collect(Collectors.toList());

        return availableUsers;
    }
    public List<Userr> getUsersAssignedLastSunday(LocalDate date) {
        LocalDate lastSunday = date.minusWeeks(1).with(DayOfWeek.SUNDAY);
        return scheduleRepository.findUsersAssignedOnLastSunday(lastSunday, ScheduleType.SOC_SHIFT);
    }



    private void verifyWeeklyWorkingHours(List<Userr> users, LocalDate startDate, LocalDate endDate) {
        for (Userr user : users) {
            // Fetch total hours for the user within the given date range
            Long totalHours = scheduleRepository.sumHoursByUser(user, startDate, endDate);

            // Assuming 40 hours is the weekly standard
            if (totalHours == null) {
                totalHours = 0L;
            }

            if (totalHours != 40) {
                logger.warn("User {} does not meet weekly working hour requirements. Total hours: {}", user.getName(), totalHours);
            }
        }
    }
    public Map<Userr, Long> verifyWeeklyWorkingHourss(List<Userr> users, LocalDate startDate, LocalDate endDate) {
        Map<Userr, Long> userHoursMap = new HashMap<>();

        for (Userr user : users) {
            // Fetch total hours for the user within the given date range
            Long totalHours = scheduleRepository.sumHoursByUser(user, startDate, endDate);

            // Assuming 40 hours is the weekly standard
            if (totalHours == null) {
                totalHours = 0L;
            }

            userHoursMap.put(user, totalHours);

            if (totalHours != 40) {
                logger.warn("User {} does not meet weekly working hour requirements. Total hours: {}", user.getName(), totalHours);
            }
        }

        return userHoursMap;
    }


    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
    public void deleteAllSchedules() {
        logger.info("Deleting all schedules...");
        scheduleRepository.deleteAll();
        logger.info("All schedules deleted successfully.");
    }

    public Map<LocalDate, Map<String, List<String>>> getFormattedSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();

        return schedules.stream().collect(Collectors.groupingBy(
                Schedule::getDate,
                Collectors.groupingBy(
                        schedule -> schedule.getShift() == null ?
                                (schedule.getType() == ScheduleType.REGULAR_JOB ? "Regular Job" : "Day-Off")
                                : schedule.getShift().getName(),
                        Collectors.mapping(schedule -> schedule.getUser() == null ? "Unknown User" : schedule.getUser().getName(), Collectors.toList())
                )
        ));

    }
    private boolean isAssignedToShiftThisWeek(Userr user, Shift shift, LocalDate date) {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);  // Get start of the week (Monday)
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);    // Get end of the week (Sunday)

        // Check if user is already assigned to the same shift in the week range
        return scheduleRepository.existsByUserAndShiftAndDateBetween(user, shift, startOfWeek, endOfWeek);
    }
    private boolean isUserIdOne(List<Userr> users, int userId, LocalDate date) {
        return users.stream().anyMatch(user -> user.getId() == userId);
    }

    private Userr getUserById(List<Userr> users, int userId) {
        return users.stream().filter(user -> user.getId() == userId).findFirst().orElse(null);
    }
    private int getTotalHoursForWeek(Userr user, LocalDate date) {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);  // Get start of the week (Monday)
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);    // Get end of the week (Sunday)

        // Calculate total hours from shifts and regular jobs
// Change int to Long to match the return type from the repository methods
        Long totalShiftHours = scheduleRepository.getTotalShiftHoursForUser(user, startOfWeek, endOfWeek);
        Long totalRegularJobHours = scheduleRepository.getTotalRegularJobHoursForUser(user, startOfWeek, endOfWeek);


        return (int) (totalShiftHours + totalRegularJobHours);
    }



}

