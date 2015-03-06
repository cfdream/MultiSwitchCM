replacement=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\memorysize vs accuracy_falseNegative\replacement_multi_targetFlowNum_vs_Overhead.txt');
figure
hold on;
scatter(replacement(:,6), replacement(:,2))

set(gca, 'FontSize', 18);
%legend('host=>switches', 'switches=>controller', 'sum');
%legend('Location', 'northwest');
xlabel('buckets in hashmap')
ylabel('false negative')
hold off;

figure
hold on;
scatter(replacement(:,6), replacement(:,3))

set(gca, 'FontSize', 18);
%legend('host=>switches', 'switches=>controller', 'sum');
%legend('Location', 'northwest');
xlabel('buckets in hashmap')
ylabel('accuracy')
hold off;

replacement=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\memorysize vs accuracy_falseNegative\replacement_samplehold_replacement_diffMemory.txt');
sampleHold=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\memorysize vs accuracy_falseNegative\sh_samplehold_replacement_diffMemory.txt');

%accuracy
figure
hold on;
errorbar(replacement(:,2), replacement(:,3)', replacement(:,4)', '-rv', 'linewidth', 2)
errorbar(sampleHold(:,2), sampleHold(:,3)', sampleHold(:,4)', '-b^', 'linewidth', 2)

set(gca, 'FontSize', 18);
legend('replacement', 'sample and hold');
legend('Location', 'northwest');
legend('boxoff');
xlabel('memory size (\itT)')
ylabel('accuracy')
hold off;

%false negative
figure
hold on;
errorbar(replacement(:,2), replacement(:,5)', replacement(:,6)', '-rv', 'linewidth', 2)
errorbar(sampleHold(:,2), sampleHold(:,5)', sampleHold(:,6)', '-b^', 'linewidth', 2)

set(gca, 'FontSize', 18);
legend('replacement', 'sample and hold');
legend('Location', 'northwest');
legend('boxoff');
xlabel('memory size (\itT)')
ylabel('false negative')
hold off;