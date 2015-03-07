replacement=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\numRecPktsToSendSignal_vs_accuracy_falseNegative\replacement_samplehold_replacement_diffMemory.txt');
sampleHold=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\numRecPktsToSendSignal_vs_accuracy_falseNegative\sh_samplehold_replacement_diffMemory.txt');

x=1:4

%accuracy
figure
hold on;
errorbar(x, replacement(:,3)', replacement(:,4)', '-rv', 'linewidth', 2)
errorbar(x, sampleHold(:,3)', sampleHold(:,4)', '-b^', 'linewidth', 2)

set(gca, 'FontSize', 18,  'XTick', [1 2 3 4], 'xticklabel', {'1000', '10000', '100000', '1000000'});
legend('replacement', 'sample and hold');
legend('Location', 'northwest');
legend('boxoff');
xlabel('memory size (\itT)')
ylabel('accuracy')
hold off;

%false negative
figure
hold on;
errorbar(x, replacement(:,5)', replacement(:,6)', '-rv', 'linewidth', 2)
errorbar(x, sampleHold(:,5)', sampleHold(:,6)', '-b^', 'linewidth', 2)

set(gca, 'FontSize', 18);
legend('replacement', 'sample and hold');
legend('Location', 'northwest');
legend('boxoff');
xlabel('memory size (\itT)')
ylabel('false negative')
hold off;