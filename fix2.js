const fs = require('fs');

let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/SohojScreens.kt', 'utf8');

// Fix the broke lines:
content = content.replace(/formatAmt\(if \(dayTotal >= 0, isBengali\) dayTotal else -dayTotal\)/g, "formatAmt(if (dayTotal >= 0) dayTotal else -dayTotal, isBengali)");
content = content.replace(/formatAmt\(maxOf\(0\.0, creditTotal - debitTotal, isBengali\)\)/g, "formatAmt(maxOf(0.0, creditTotal - debitTotal), isBengali)");
content = content.replace(/formatAmt\(if \(totalDues >= 0, isBengali, 2\) totalDues else -totalDues\)/g, "formatAmt(if (totalDues >= 0) totalDues else -totalDues, isBengali, 2)");


fs.writeFileSync('app/src/main/java/com/example/ui/screens/SohojScreens.kt', content, 'utf8');
