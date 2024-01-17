import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public class print {
    public static void main(String[] args) throws ParseException {
        String[] input = {
            "WFS000581 Active 09/14/2023 10:30 AM 09/14/2023 02:30 PM 4:00 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/14/2023 03:00 PM 09/14/2023 07:24 PM 4:24 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581",
            "WFS000581 Active 09/14/2023 03:00 PM 09/14/2023 07:24 PM 4:24 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/15/2023 10:28 AM 09/15/2023 02:30 PM 4:02 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/15/2023 03:00 PM 09/15/2023 04:47 PM 1:47 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/16/2023 10:40 AM 09/16/2023 02:30 PM 3:50 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/16/2023 03:00 PM 09/16/2023 07:49 PM 4:49 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/17/2023 10:40 AM 09/17/2023 02:30 PM 3:50 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/17/2023 03:00 PM 09/17/2023 07:49 PM 4:49 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581", 
            "WFS000581 Active 09/18/2023 10:40 AM 09/18/2023 02:30 PM 3:50 09/10/2023 09/23/2023 HerWaWdez SaWchez, AWdrew AWMhEWy 000581"
        };

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Map<String, Map<String, List<Date>>> employeeShifts = new HashMap<>();

        for (String record : input) {
            String[] fields = record.split(" ");
            String employeeName = fields[13] + " " + fields[14]; // Assuming the employee name is at the 14th and 15th position
            String position = fields[15]; // Assuming the position is at the 16th position
            Date shiftStart = sdf.parse(fields[2] + " " + fields[3] + " " + fields[4]);
            Date shiftEnd = sdf.parse(fields[5] + " " + fields[6] + " " + fields[7]);

            if (!employeeShifts.containsKey(employeeName)) {
                employeeShifts.put(employeeName, new HashMap<>());
            }
            if (!employeeShifts.get(employeeName).containsKey(position)) {
                employeeShifts.get(employeeName).put(position, new ArrayList<>());
            }
            employeeShifts.get(employeeName).get(position).add(shiftStart);
            employeeShifts.get(employeeName).get(position).add(shiftEnd);
        }

        for (Map.Entry<String, Map<String, List<Date>>> entry : employeeShifts.entrySet()) {
            String employeeName = entry.getKey();
            Map<String, List<Date>> positions = entry.getValue();

            for (Map.Entry<String, List<Date>> positionEntry : positions.entrySet()) {
                String position = positionEntry.getKey();
                List<Date> shifts = positionEntry.getValue();
                Collections.sort(shifts);

                // Check for 7 consecutive days
                int consecutiveDays = 1;
                for (int i = 1; i < shifts.size(); i += 2) {
                    long diffInMillies = Math.abs(shifts.get(i).getTime() - shifts.get(i - 1).getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (diff == 1) {
                        consecutiveDays++;
                        if (consecutiveDays == 7) {
                            System.out.println(employeeName + " at position " + position + " has worked for 7 consecutive days.");
                            break;
                        }
                    } else {
                        consecutiveDays = 1;
                    }
                }

                // Check for less than 10 hours but more than 1 hour between shifts
                for (int i = 2; i < shifts.size(); i += 2) {
                    long diffInMillies = Math.abs(shifts.get(i).getTime() - shifts.get(i - 1).getTime());
                    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (diff > 1 && diff < 10) {
                        System.out.println(employeeName + " at position " + position + " has less than 10 hours but more than 1 hour between shifts.");
                        break;
                    }
                }

                // Check for more than 14 hours in a single shift
                for (int i = 0; i < shifts.size(); i += 2) {
                    long diffInMillies = Math.abs(shifts.get(i + 1).getTime() - shifts.get(i).getTime());
                    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (diff > 14) {
                        System.out.println(employeeName + " at position " + position + " has worked for more than 14 hours in a single shift.");
                        break;
                    }
                }
            }
        }
    }
}